import java.util.Random;
/*
 * 笔试西山居的时候，遇到这个题，很有趣
 * 两个角色对打，打桩打到一个暴毙为止
 * 角色   血量   攻击力   攻速
 * A     2500    8       4次/秒
 * B     2500    10      3次/秒
 *
 * A技能：马上造成35伤害，并有30%几率让对手攻速降低一半，持续5秒，CD5秒
 * B技能：马上造成45伤害，并有20%几率让自己攻速加快一倍，持续5秒，CD4.5秒
 */

class Role{
    int blood;
    int attack;
    int attackSpeed;

    @Override
    public String toString() {
        return "Role{blood=" + blood + ", attack=" + attack + ", attackSpeed=" + attackSpeed + '}';
    }
}

abstract class Skill{
    Role role;
    Role otherRole;
    int skillCd;
    int tempCd;

    public int getSkillCd() {
        return skillCd;
    }

    Skill(Role role, Role otherRole){
        this.role=role;
        this.otherRole=otherRole;
    }

    abstract void operate();
}

class AttackSkill extends Skill{
    AttackSkill(Role role, Role otherRole) {
        super(role, otherRole);
    }

    @Override
    public int getSkillCd() {
        return role.attackSpeed;
    }

    @Override
    void operate(){
        otherRole.blood-=role.attack;
        tempCd=role.attackSpeed;
    }
}



public class RolePlay{
    private static final int A_ROLE_ATTACK=0;
    private static final int B_ROLE_ATTACK=1;

    private static final int A_ROLE_SKILL=2;
    private static final int B_ROLE_SKILL=3;

    private static final int A_ROLE_SKILL_TIME=4;
    private static final int B_ROLE_SKILL_TIME=5;

    public static void main(String[] args) {
        RolePlay rolePlay = new RolePlay();
        int aWin=0;
        int bWin=0;
        for (int i = 0; i < 100000; i++) {
            Role[] play = rolePlay.play();
            if (play[0].blood>play[1].blood) {
                aWin++;
            }else{
                bWin++;
            }
        }
        System.out.println("a win count:"+aWin);
        System.out.println("b win count:"+bWin);

    }

    private Random random=new Random();

    private int timeCount=0;

    public Role[] play(){
        Skill[] skills=new Skill[6];
        Role aRole=new Role();
        Role bRole=new Role();
        aRole.blood=2500;
        aRole.attack=8;
        aRole.attackSpeed=1000/4;
        bRole.blood=2500;
        bRole.attack=10;
        bRole.attackSpeed=1000/3;
        skills[A_ROLE_ATTACK]=new AttackSkill(aRole,bRole);
        skills[B_ROLE_ATTACK]=new AttackSkill(bRole,aRole);
        skills[A_ROLE_SKILL]=new Skill(aRole,bRole){
            @Override
            void operate() {
                otherRole.blood-=35;
                if(random.nextInt(100)<30){
                    otherRole.attackSpeed*=2;
                    skills[A_ROLE_SKILL_TIME].tempCd=5000;
                }
                skills[A_ROLE_SKILL].tempCd=skills[A_ROLE_SKILL].skillCd;
            }
        };
        skills[A_ROLE_SKILL].skillCd=5000;

        skills[A_ROLE_SKILL_TIME]=new Skill(aRole,bRole) {
            @Override
            void operate() {
                otherRole.attackSpeed/=2;
                skills[A_ROLE_SKILL_TIME].tempCd=-1;
            }
        };
        skills[A_ROLE_SKILL_TIME].skillCd=5000;
        skills[A_ROLE_SKILL_TIME].tempCd=-1;


        skills[B_ROLE_SKILL]=new Skill(bRole,aRole){
            @Override
            void operate() {
                otherRole.blood-=45;
                if(random.nextInt(100)<20){
                    role.attackSpeed/=2;
                    skills[B_ROLE_SKILL_TIME].tempCd=5000;
                }
                skills[B_ROLE_SKILL].tempCd=skills[B_ROLE_SKILL].skillCd;
            }
        };
        skills[B_ROLE_SKILL].skillCd=4500;

        skills[B_ROLE_SKILL_TIME]=new Skill(bRole,aRole) {
            @Override
            void operate() {
                role.attackSpeed*=2;
                skills[B_ROLE_SKILL_TIME].tempCd=-1;
            }
        };
        skills[B_ROLE_SKILL_TIME].skillCd=5000;
        skills[B_ROLE_SKILL_TIME].tempCd=-1;


        while(aRole.blood>0 && bRole.blood>0) {
            int minTempCd = Integer.MAX_VALUE;
            int minIndex = 0;
            for (int i = 0; i < skills.length; i++) {
                if (skills[i].tempCd >= 0 && skills[i].tempCd < minTempCd) {
                    minTempCd = skills[i].tempCd;
                    minIndex = i;
                }
            }
            for (Skill skill : skills) {
                skill.tempCd -= minTempCd;
            }
            skills[minIndex].operate();
            timeCount+=minTempCd;
        }
        timeCount=0;
        return new Role[]{aRole,bRole};
    }

}
