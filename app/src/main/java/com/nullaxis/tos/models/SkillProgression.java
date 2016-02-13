package com.nullaxis.tos.models;

public class SkillProgression {
    private Skill skill;
    private int current_circle;
    private int current_rank;
    private int max_rank;

    public SkillProgression(Skill skill, int current_circle) {
        this.skill = skill;
        this.current_circle = current_circle;
        current_rank = 0;
        max_rank = calcMaxRank();
    }

    public void rankUp(){
        if (current_rank < max_rank){
            current_rank++;
        }
    }

    public void rankDown(){
        if (current_rank > 0){
            current_rank--;
        }
    }

    private int calcMaxRank(){
        return Math.min(skill.getMax_level(), skill.getRank_per_circle() *
                (current_circle - skill.getCircle() + 1));
    }

    public void updateCircle(int currentCircle) {
        this.current_circle = currentCircle;
        max_rank = calcMaxRank();
        if (current_rank > max_rank) current_rank = max_rank;
    }

    public Skill getSkill() {
        return skill;
    }

    public int getCurrent_circle() {
        return current_circle;
    }

    public int getCurrent_rank() {
        return current_rank;
    }

    public int getMax_rank() {
        return max_rank;
    }

    public void setCurrent_rank(int current_rank) {
        this.current_rank = current_rank;
    }
}
