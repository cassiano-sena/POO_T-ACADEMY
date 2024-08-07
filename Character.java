import java.util.ArrayList;
import java.util.List;
import java.util.Random;

abstract class Character {
    protected String name;
    protected String origin;
    protected Classes selectedClass;
    protected int health;
    protected Item equippedItem;
    protected String status;
    private List<StatusEffect> statusEffects;
    private boolean actionTaken;
    private boolean waitNextTurn;
    private boolean exhaustedNextTurn;
    protected int experiencePoints;
    protected int level;

    public Character(String name, String origin, Classes selectedClass, Item equippedItem, String status) {
        this.name = name;
        this.origin = origin;
        this.selectedClass = selectedClass;
        this.health = selectedClass.getMaxHealth();
        this.equippedItem = equippedItem;
        this.status = status;
        this.statusEffects = new ArrayList<>();
        this.actionTaken = false;
        this.waitNextTurn = false;
        this.exhaustedNextTurn = false;
        this.experiencePoints = 0;
        this.level = 1;
    }

    public void addStatusEffect(StatusEffect statusEffect, int duration) {
        statusEffect.setDuration(duration);
        statusEffects.add(statusEffect);
        statusEffect.applyEffect(this);
    }

    public void addExperience(int exp) {
        this.experiencePoints += exp;
        checkLevelUp(); // Após adicionar experiência, verifica se houve subida de nível
    }

    public void checkLevelUp() {
        if (experiencePoints >= 100 * level) {
            level++;
            System.out.println(name + " subiu para o nível " + level + "!");

            // Atualiza os atributos da classe selecionada do jogador
            updatePlayerAttributes();
        }
    }

    private void updatePlayerAttributes() {
        int oldStrength = selectedClass.getStrength();
        int oldMaxHealth = selectedClass.getMaxHealth();
        int oldDexterity = selectedClass.getDexterity();
        int oldDefense = selectedClass.getDefense();
        int oldIntelligence = selectedClass.getIntelligence();

        // Aumenta os atributos da classe selecionada do jogador
        selectedClass.setStrength(oldStrength + 5);
        selectedClass.setMaxHealth(oldMaxHealth + 5);
        selectedClass.setDexterity(oldDexterity + 5);
        selectedClass.setDefense(oldDefense + 5);
        selectedClass.setIntelligence(oldIntelligence + 5);

        // Atualiza a saúde do jogador com a nova saúde máxima
        setHealth(selectedClass.getMaxHealth());

        // Exibe as mudanças nos atributos
        System.out.println("Força: " + oldStrength + " -> " + selectedClass.getStrength());
        System.out.println("Destreza: " + oldDexterity + " -> " + selectedClass.getDexterity());
        System.out.println("Vida Máxima: " + oldMaxHealth + " -> " + selectedClass.getMaxHealth());
        System.out.println("Defesa: " + oldDefense + " -> " + selectedClass.getDefense());
        System.out.println("Inteligência: " + oldIntelligence + " -> " + selectedClass.getIntelligence());
    }
    public void updateStatusEffects() {
        boolean anyStatusActive = false; // Variável para verificar se algum status está ativo

        for (StatusEffect effect : new ArrayList<>(statusEffects)) {
            effect.decreaseDuration();
            if (!effect.isActive()) {
                statusEffects.remove(effect);
                System.out.println(name + " não está mais " + effect.getName() + ".");
                clearStatus(effect.getName());
            } else {
                anyStatusActive = true; // Se ainda há algum status ativo
                System.out.println(name + " está sendo afetado por " + effect.getName() + ".");
            }
        }

        // Se nenhum status estiver ativo, imprimir que não há status ativos
        if (!anyStatusActive) {
            System.out.println(name + " não está sob efeitos de status.");
        }
    }


    public List<String> getStatusEffects() {
        List<String> activeEffects = new ArrayList<>();
        for (StatusEffect effect : statusEffects) {
            activeEffects.add(effect.getName());
        }
        return activeEffects;
    }

    public boolean isActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(boolean actionTaken) {
        this.actionTaken = actionTaken;
    }

    public boolean isWaitingNextTurn() {
        return waitNextTurn;
    }

    public boolean isExhaustedNextTurn() {
        return exhaustedNextTurn;
    }

    public void setExhaustedNextTurn(boolean exhaustedNextTurn) {
        this.exhaustedNextTurn = exhaustedNextTurn;
    }

    public void setWaitNextTurn(boolean waitNextTurn) {
        this.waitNextTurn = waitNextTurn;
    }

    public void takeDamage(int damage) {
        this.health = Math.max(0, this.health - damage);
    }

    public void setSleeping(boolean sleeping, int duration) {
        if (sleeping) {
            addStatusEffect(new SleepStatus(duration), duration);
        } else {
            clearStatus("dormindo");
        }
    }

    public void setBurning(boolean burning, int duration) {
        if (burning) {
            addStatusEffect(new BurnStatus(duration), duration);
        } else {
            clearStatus("queimado");
        }
    }

    public void setBleeding(boolean bleeding, int duration, int bleedDamage) {
        if (bleeding) {
            addStatusEffect(new BleedEffect(duration, bleedDamage), duration);
        } else {
            clearStatus("sangrando");
        }
    }

    public void setStunned(boolean stunned, int duration) {
        if (stunned) {
            addStatusEffect(new StunStatus(duration), duration);
        } else {
            clearStatus("atordoado");
        }
    }

    public void setPoisoned(boolean poisoned, int duration) {
        if (poisoned) {
            addStatusEffect(new PoisonStatus(duration), duration);
        } else {
            clearStatus("envenenado");
        }
    }
    public void setThorns(boolean hasThorns, int duration, int thornDamage) {
        if (hasThorns) {
            addStatusEffect(new ThornsEffect(duration), duration);
        } else {
            clearStatus("espinhos");
        }
    }

    public void setRecoil(boolean hasRecoil, int duration, int recoilDamage) {
        if (hasRecoil) {
            addStatusEffect(new RecoilEffect(duration), duration);
        } else {
            clearStatus("recoil");
        }
    }
//    public void setFieryWeapon(boolean hasFieryWeapon, int duration, int fireChance, int fireDamage) {
//        if (hasFieryWeapon) {
//            addStatusEffect(new FieryWeaponEffect(duration, fireChance, fireDamage), duration);
//        } else {
//            clearStatus("imbuiu sua arma com fogo");
//        }
//    }
//
//    public void setPoisonousWeapon(boolean hasPoisonousWeapon, int duration, int poisonChance, int poisonDamage) {
//        if (hasPoisonousWeapon) {
//            addStatusEffect(new PoisonousWeaponEffect(duration, poisonChance, poisonDamage), duration);
//        } else {
//            clearStatus("imbuiu sua arma com veneno");
//        }
//    }

    private void clearStatus(String statusName) {
        this.status = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Classes getSelectedClass() {
        return selectedClass;
    }

    public void setSelectedClass(Classes selectedClass) {
        this.selectedClass = selectedClass;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public Item getEquippedItem() {
        return equippedItem;
    }

    public void setEquippedItem(Item equippedItem) {
        this.equippedItem = equippedItem;
    }

    public void setEquippedItemById(int itemId) {
        Item itemToEquip = ItemManager.getItemById(itemId);

        if (itemToEquip != null) {
            if (this.equippedItem != null) {
                System.out.println(name + " removeu " + this.equippedItem.getName() + ".");
            }

            this.equippedItem = itemToEquip;
            System.out.println(name + " equipou " + itemToEquip.getName() + ".");
        } else {
            System.out.println("Item com ID " + itemId + " não encontrado.");
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public abstract void useSkill();

    void waitFor(int seconds) {
        try {
            Thread.sleep(seconds * 1000); // Converte segundos para milissegundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void attack(int actionIndex, Character attacker, Character target) {
        if (equippedItem == null) {
            System.out.println("Nenhum item equipado.");
            return;
        }

        List<Attack> itemActions = equippedItem.getItemActions();

        if (actionIndex >= 0 && actionIndex < itemActions.size()) {
            Attack selectedAction = itemActions.get(actionIndex);
            System.out.println("Ataque selecionado: " + selectedAction.getDescription());

//            if (equippedItem.hasFieryWeaponEffect()) {
//                setFieryWeapon(true, 3, 50, 5);
//            } else if (equippedItem.hasPoisonousWeaponEffect()) {
//                setPoisonousWeapon(true, 3, 50, 5);
//            }

            // Executa o ataque usando a classe Attack
            selectedAction.attack(attacker, target);
        } else {
            System.out.println("Ação inválida.");
        }
    }

    protected void heal() {
        Random random = new Random();
        int healAmount = random.nextInt(10) + 1; // Restaura entre 1 e 10 pontos de vida
        int newHealth = this.health + healAmount;

        if (newHealth > selectedClass.getMaxHealth()) {
            newHealth = selectedClass.getMaxHealth();
        }

        this.health = newHealth;

        System.out.println(this.name + " usou uma poção e restaurou " + healAmount + " de vida!");
        System.out.println("Vida atual: " + newHealth + "/" + selectedClass.getMaxHealth());
    }

}
