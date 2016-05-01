import shutil
import sqlite3
import ujson
from logging import getLogger
from os import path

import requests
from bs4 import BeautifulSoup, Tag

l = getLogger(__name__)

DB_FILE = "/Users/Ramiro/ToSDatabase.db"
SOURCE_DIR = "/Users/Ramiro/Desktop"
IMAGE_DIR = "/Users/Ramiro/Documents/ToSIcons"
CLASSES_FILE = "classes.json"
CLASSES_PATH = path.join(SOURCE_DIR, CLASSES_FILE)
RANKS_FILE = "ranks.json"
RANKS_PATH = path.join(SOURCE_DIR, RANKS_FILE)
SKILLS_FILE = "skills.json"
SKILLS_PATH = path.join(SOURCE_DIR, SKILLS_FILE)
ABILITIES_JSON_FILE = "abilities.json"
ABILITIES_JSON_PATH = path.join(SOURCE_DIR, ABILITIES_JSON_FILE)
ABILITIES_HTML_FILE = "abilities.html"
ABILITIES_HTML_PATH = path.join(SOURCE_DIR, ABILITIES_HTML_FILE)
SKILL_ABILITIES_FILE = "skillsAbilities.json"
SKILL_ABILITIES_PATH = path.join(SOURCE_DIR, SKILL_ABILITIES_FILE)

ICONS_BASE_URL = "http://www.tosbase.com/content/img/icons"
ICONS_CLASSES_URL = "classes"
ICONS_SKILLS_URL = "skills"
ICONS_ABILITIES_URL = "skills"

ARCH_WARRIOR_ID = 1
ARCH_WIZARD_ID = 2
ARCH_ARCHER_ID = 3
ARCH_CLERIC_ID = 4

MAX_RANK = 99999


def get_value(o, object_name, value_name, value_type):
    """
    Get value from an object and handle exceptions.
    :param o:
    :param object_name:
    :param value_name:
    :param value_type:
    :return:
    """
    try:
        return o[value_name]
    except KeyError:
        l.warning("The field {} doesn't exists in {}: {}".format(value_name, object_name, ujson.dumps(o)))
        return value_type()


def get_archetype_id(archetype):
    """
    Get the hardcoded ID based on the archetype name.
    :param archetype:
    :return:
    """
    archetype = archetype.lower()
    if archetype == 'warrior':
        return ARCH_WARRIOR_ID
    elif archetype == 'wizard':
        return ARCH_WIZARD_ID
    elif archetype == 'archer':
        return ARCH_ARCHER_ID
    elif archetype == 'cleric':
        return ARCH_CLERIC_ID
    else:
        l.error("There is no valid Archetype ID for: {}".format(archetype))


def get_skill_class(cursor, _class):
    """
    Get the skill class from the DB.
    :param cursor:
    :param _class:
    :return:
    """
    cursor.execute('SELECT id FROM classes WHERE temp_id = ?', (_class,))
    data = cursor.fetchone()
    try:
        return data[0]
    except TypeError:
        l.error("The Class {} doesn't exists.".format(_class))


def get_ability_skill(cursor, skill):
    """
    Get the ability skill from the DB.
    :param cursor:
    :param skill:
    :return:
    """
    cursor.execute('SELECT id FROM skills WHERE identifier = ?', (skill,))
    data = cursor.fetchone()
    try:
        return data[0]
    except TypeError:
        l.error("The Skill {} doesn't exists.".format(skill))
        return 0


def format_icon(icon):
    """
    Reformat the icon string to be valid for Android usage
    :param icon:
    :return:
    """
    return icon.strip().replace(" ", "_")


def insert_archetypes(cursor):
    """
    The archetypes are hardcoded
    :param cursor:
    :return:
    """
    archetypes = (
        (ARCH_WARRIOR_ID, "Warrior", "c_warrior_swordsman", 0),
        (ARCH_WIZARD_ID, "Wizard", "c_wizard_wizard", 0),
        (ARCH_ARCHER_ID, "Archer", "c_archer_archer", 0),
        (ARCH_CLERIC_ID, "Cleric", "c_cleric_cleric", 0)
    )

    cursor.executemany("INSERT INTO archetypes VALUES (?, ?, ?, ?)", archetypes)


def insert_classes(cursor):
    """
    Fetch and insert the classes from classes.json
    :param cursor:
    :return:
    """
    ranks = dict()
    with open(RANKS_PATH, encoding='UTF-8') as ranks_file:
        ranks_dict = ujson.load(ranks_file)
        for rank, ranked_archetypes in ranks_dict.items():
            try:
                rank = int(rank.strip("Rank"))
            except ValueError:
                rank = MAX_RANK
            for ranked_classes in ranked_archetypes.values():
                for ranked_class in ranked_classes:
                    ranks[ranked_class] = rank

    with open(CLASSES_PATH, encoding='UTF-8') as classes_file:
        classes_dict = ujson.load(classes_file)
        classes = list()
        # Get list of sorted classes
        sorted_classes_ids = list()
        for class_id in classes_dict.keys():
            if '_' in class_id:
                splited_class_id = class_id.split("_", 1)
                sorted_classes_ids.append((class_id, int(splited_class_id[0].strip("Char")), int(splited_class_id[-1])))
            else:
                sorted_classes_ids.append((class_id, 0, 0))
        sorted_classes_ids.sort(key=lambda tup: tup[2])
        sorted_classes_ids.sort(key=lambda tup: tup[1])
        # Start processing them
        for class_id, archetype, char_n in sorted_classes_ids:
            _class = classes_dict[class_id]
            class_info = list()
            # Get Class Name
            class_info.append(get_value(_class, "Class", "name", str))
            # Get Class Archetype
            class_info.append(get_archetype_id(get_value(_class, "Class", "base", str)))
            # Get Rank
            class_info.append(ranks.get(class_id, 0))
            # Get Icon
            class_info.append(format_icon(get_value(_class, "Class", "icon", str)))
            # Get Temp ID
            class_info.append(class_id)

            classes.append(tuple(class_info))

        classes = tuple(classes)

        cursor.executemany("INSERT INTO classes (name, archetype, rank, icon, temp_id) VALUES (?, ?, ?, ?, ?)", classes)


def insert_skills(cursor):
    """
    Fetch and insert the skills from skills.json
    :param cursor:
    :return:
    """
    # Get the class of every skill
    skills_classes = dict()
    with open(CLASSES_PATH, encoding='UTF-8') as classes_file:
        classes_dict = ujson.load(classes_file)
        for class_id, _class in classes_dict.items():
            class_skills = _class.get("skills", list())
            for class_skill in class_skills:
                skills_classes[class_skill.lower()] = class_id

    with open(SKILLS_PATH, encoding='UTF-8') as skills_file:
        skills_dict = ujson.load(skills_file)
        skills = list()
        # Get list of sorted skills
        sorted_skills_ids = list()
        for skill_id, skill in skills_dict.items():
            if skill_id:
                sorted_skills_ids.append((skill_id, int(skill.get("id", 0))))
            else:
                sorted_skills_ids.append((skill_id, 0))
        sorted_skills_ids.sort(key=lambda tup: tup[1])
        # Start processing them
        for skill_id, _ in sorted_skills_ids:
            skill = skills_dict[skill_id]
            skill_info = list()
            # Get Skill Id
            skill_info.append(int(get_value(skill, "Skill", "id", str)))
            # Get Skill Name
            skill_info.append(get_value(skill, "Skill", "name", str))
            # Get Skill Identifier
            identifier = get_value(skill, "Skill", "ident", str).lower()
            skill_info.append(identifier)
            # Get Skill Icon
            skill_info.append(format_icon(get_value(skill, "Skill", "icon", str)))
            # Get Skill Circle
            skill_info.append(int(get_value(skill, "Skill", "circle", str)))
            # Get Skill Rank Level
            skill_info.append(int(get_value(skill, "Skill", "rankLevel", str)))
            # Get Skill Max Level
            skill_info.append(int(get_value(skill, "Skill", "maxLevel", str)))
            # Get Skill Video
            skill_info.append(get_value(skill, "Skill", "video", str))
            # Get Skill Desc
            skill_info.append(get_value(skill, "Skill", "desc", str))
            # Get Skill Details
            skill_info.append(get_value(skill, "Skill", "desc2", str))
            # Get Skill Type 1
            skill_info.append(get_value(skill, "Skill", "type1", str))
            # Get Skill Type 2
            skill_info.append(get_value(skill, "Skill", "type2", str))
            # Get Skill Cooldown
            skill_info.append(get_value(skill, "Skill", "cooldown", int))
            # Get Skill Element
            skill_info.append(get_value(skill, "Skill", "element", str))
            # Get Skill Required Stance
            skill_info.append(get_value(skill, "Skill", "reqStance", str))
            # Get Skill Level List
            skill_info.append(ujson.dumps(get_value(skill, "Skill", "levelList", dict)))
            # Get Skill Use Overheat
            skill_info.append(get_value(skill, "Skill", "useOverHeat", int))
            # Get Skill Class
            skill_info.append(get_skill_class(cursor, skills_classes.get(identifier, '')))


            skills.append(tuple(skill_info))

        skills = tuple(skills)

        cursor.executemany("INSERT INTO skills (id, name, identifier, icon, circle, rank_level, max_level, video, "
                           "desc, details, type1, type2, cooldown, element, req_stance, level_list, use_overheat, "
                           "class) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", skills)


def insert_skill_abilities(cursor):
    """
    Fetch and insert the skill abilities from abilities.json and abilities.html
    :param cursor:
    :return:
    """
    # Get the skill of every ability
    abilities_skills = dict()
    with open(SKILL_ABILITIES_PATH, encoding='UTF-8') as skills_file:
        skills_dict = ujson.load(skills_file)
        for skill_id, skill_abilities in skills_dict.items():
            for skill_ability in skill_abilities:
                abilities_skills[skill_ability.lower()] = skill_id.lower()

    # Get info from HTML
    abilities_html_dict = dict()
    with open(ABILITIES_HTML_PATH, encoding='UTF-8') as abilities_html_file:
        soup = BeautifulSoup(abilities_html_file, 'html.parser')
        for ability in soup.findAll('div'):
            # Remove clutter from attribute ID
            ability_id = ability.attrs['id'][18:-8]
            ability_name = ability.b.text
            ability_type = ''
            ability_max_level = 0
            ability_req_skill_level = 0
            ability_desc = ability.contents[-1].strip()
            # Parse all except the name and desc that we already got
            for i in range(2, len(ability.contents)-2):
                if isinstance(ability.contents[i], Tag):
                    if ability.contents[i].text == "Type:":
                        ability_type = ability.contents[i+1].strip()
                    elif ability.contents[i].text == "Max Level:":
                        ability_max_level = int(ability.contents[i+1].strip())
                    elif ability.contents[i].text == "Required Skill Level:":
                        ability_req_skill_level = int(ability.contents[i+1].strip())
                    elif ability.contents[i].text == "Circle:":
                        pass
                    else:
                        if ability.contents[i].name != 'br':
                            l.warning("There is a non handled tag {} in ability: {}".format(ability.contents[i].text,
                                                                                            ability))
            abilities_html_dict[ability_id.lower()] = {
                'name': ability_name,
                'type': ability_type,
                'max_level': ability_max_level,
                'req_skill_level': ability_req_skill_level,
                'desc': ability_desc
            }

    with open(ABILITIES_JSON_PATH, encoding='UTF-8') as abilities_file:
        abilities_dict = ujson.load(abilities_file)
        abilities = list()
        # Get list of sorted abilities
        sorted_abilities_ids = list()
        for ability_id, ability in abilities_dict.items():
            if ability_id:
                sorted_abilities_ids.append((ability_id, int(ability.get("ClassID", 0))))
            else:
                sorted_abilities_ids.append((ability_id, 0))
        sorted_abilities_ids.sort(key=lambda tup: tup[1])
        # Start processing them
        for ability_id, _ in sorted_abilities_ids:
            ability = abilities_dict[ability_id]
            html_ability = abilities_html_dict.get(ability.get("ClassName", "").lower(), dict())
            ability_info = list()
            # Get Ability Id
            ability_info.append(int(get_value(ability, "Ability", "ClassID", str)))
            # Get Ability Name
            ability_info.append(get_value(html_ability, "Ability", "name", str))
            # Get Ability Type
            ability_info.append(get_value(html_ability, "Ability", "type", str))
            # Get Ability Required Circle
            ability_info.append(int(get_value(ability, "Ability", "ReqCircle", int)))
            # Get Ability Max Level
            ability_info.append(get_value(html_ability, "Ability", "max_level", int))
            # Get Ability Desc
            ability_info.append(get_value(html_ability, "Ability", "desc", str))
            # Get Ability Icon
            ability_info.append(format_icon(get_value(ability, "Ability", "Icon", str)))
            # Get Skill Class
            ability_info.append(get_ability_skill(cursor, abilities_skills.get(ability_id.lower(), '')))
            # Get Ability Required Skill Level
            ability_info.append(get_value(html_ability, "Ability", "req_skill_level", int))

            abilities.append(tuple(ability_info))

        abilities = tuple(abilities)

        cursor.executemany("INSERT INTO skill_abilities (id, name, type, circle, max_level, desc, icon, skill_id, "
                           "req_skill_level) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", abilities)


def save_icons(icons, entity, append_icon=False):
    """

    :param icons:
    :param entity:
    :param append_icon:
    :return:
    """
    for icon in icons:
        if append_icon:
            icon_url = "{}/{}/icon_{}.png".format(ICONS_BASE_URL, entity, icon[0])
        else:
            icon_url = "{}/{}/{}.png".format(ICONS_BASE_URL, entity, icon[0])
        r = requests.get(icon_url, stream=True)
        if r.status_code == 200:
            file_path = "{}/{}/{}.png".format(IMAGE_DIR, entity, icon[0])
            with open(file_path, 'wb') as f:
                shutil.copyfileobj(r.raw, f)
            l.info("Downloaded {}".format(icon[0]))
        else:
            l.warning("There was an issue trying to download {}".format(icon[0]))

        del r


def download_icons(cursor):
    """
    Download the icons for all the entities
    :param cursor:
    :return:
    """
    # Get all classes icons
    cursor.execute("SELECT icon FROM classes")
    icons = cursor.fetchall()
    save_icons(icons, ICONS_CLASSES_URL)
    # Get all skills icons
    cursor.execute("SELECT icon FROM skills")
    icons = cursor.fetchall()
    save_icons(icons, ICONS_SKILLS_URL, append_icon=True)
    # Get all skills icons
    cursor.execute("SELECT icon FROM skill_abilities")
    icons = cursor.fetchall()
    save_icons(icons, ICONS_ABILITIES_URL)

con = sqlite3.connect(DB_FILE)
cur = con.cursor()
try:
    insert_archetypes(cur)
    insert_classes(cur)
    insert_skills(cur)
    insert_skill_abilities(cur)
    #download_icons(cur)
    cur.execute('SELECT * FROM archetypes')
    data = cur.fetchall()
    print(data)
    con.commit()
except sqlite3.Error as e:
    print(e)

if con:
    con.close()
