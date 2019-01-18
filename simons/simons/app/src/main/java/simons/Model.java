package simons;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Created by wangtom on 2017-11-22.
 */

public class Model extends Observable {

    private static final Model Instance = new Model(4, Boolean.TRUE);

    static Model getInstance() {
        return Instance;
    }

    public enum Difficulty {EASY, NORMAL, HARD};
    public enum State {START, COMPUTER, HUMAN, LOSE, WIN};
    private State state;
    private Integer score, length, buttons, index, maxButtonNumber;
    private Vector<Integer> sequence;
    private Boolean debug;
    private Difficulty difficulty;

    private void init(Integer _buttons, Boolean _debug) {
        debug = _debug;
        length = 1;
        buttons = _buttons;
        state = State.START;
        score = 0;
        difficulty = Difficulty.NORMAL;
        maxButtonNumber = 6;
        sequence = new Vector<Integer>();

        if (debug) {
            System.out.println("[DEBUG] starting " + buttons.toString() + " button game");
        }
    }

    // constructor
    public Model(int _buttons) {
        init(_buttons, Boolean.FALSE);
    }

    public Model(int _buttons, Boolean _debug) {
        init(_buttons, _debug);
    }

    // getters and setters
    public Integer getDifficultyAsTime() {
        switch (difficulty) {
            case EASY:
                return 1500;
            case NORMAL:
                return 1000;
            case HARD:
                return 800;
        }
        return 1000;
    }

    public Vector<Integer> getSequence() { return sequence; }

    public Integer getIndex() { return index; }

    public Integer getMaxButtonNumber() { return maxButtonNumber; }

    public void setButtons(Integer buttons) {
        this.buttons = buttons;
        setChangedAndNotify();
    }

    public Integer getButtons() {
        return buttons;
    }

    public void setDifficultyByNum(Integer difficulty) {
        switch (difficulty) {
            case 0:
                setDifficulty(Difficulty.EASY);
                break;
            case 1:
                setDifficulty(Difficulty.NORMAL);
                break;
            case 2:
                setDifficulty(Difficulty.HARD);
                break;
        }
    }

    public void setDifficulty (Difficulty difficulty) {
        this.difficulty = difficulty;
        setChangedAndNotify();
    }

    public Difficulty getDifficulty() { return  difficulty; }

    public Integer getDifficultyAsNum() {
        switch (difficulty) {
            case EASY:
                return 0;
            case NORMAL:
                return 1;
            case HARD:
                return 2;
        }
        return -1;
    }

    public State getState() {
        return state;
    }

    public Integer getScore() {
        return score;
    }

    public String getStateAsString() {
        switch (getState()) {
            case START:
                return "START";
            case COMPUTER:
                return "COMPUTER";
            case HUMAN:
                return "HUMAN";
            case LOSE:
                return "LOSE";
            case WIN:
                return "WIN";
            default:
                return "Unknown State";
        }
    }

    public void newRound() {
        if (debug) System.out.println("[DEBUG] newRound, Model::state" + getStateAsString());
        if (state == State.LOSE) {
            if (debug) System.out.println("[DEBUG] reset length and score after loss");
            length = 1;
            score = 0;
        }
        sequence.clear();
        if (debug) System.out.print("[DEBUG] new sequence: ");
        for (int i = 0; i < length; ++i) {
            Integer b = ThreadLocalRandom.current().nextInt(buttons);
            sequence.add(b);
            if (debug) System.out.print(b);
        }
        if (debug) System.out.println();
        index = 0;
        state = State.COMPUTER;
        setChangedAndNotify();
    }

    public Integer nextButton() {
        if (state != State.COMPUTER) {
            System.out.println("[WARNING] nextButton called in " + getStateAsString());
            return -1;
        }

        Integer button = sequence.get(index);
        if (debug) {
            System.out.println("[DEBUG] nextButton:  index " + index + " button " + button);
        }

        index++;
        if (index >= sequence.size()) {
            index = 0;
            state = State.HUMAN;
        }
        setChangedAndNotify();
        return button;
    }

    public Boolean verifyButton(Integer button) {
        if (state != State.HUMAN) {
            System.out.println("[WARNING] verifyButton called in " + getStateAsString());
            return false;
        }

        Boolean correct = (button.equals(sequence.get(index)));

        if (debug) {
            System.out.print("[DEBUG] verifyButton: index " + index +
                    ", pushed " + button + ", sequence " +
                    sequence.get(index));
        }

        index++;
        if (!correct) {
            state = State.LOSE;
            if (debug) {
                System.out.println(", wrong");
                System.out.println("[DEBUG] state is now " + getStateAsString());
            }
        }
        else {
            if (debug) System.out.println(", correct.");
            if (index == sequence.size()) {
                state = State.WIN;
                score++;
                length++;
                if (debug) {
                    System.out.println("[DEBUG] state is now " + getStateAsString());
                    System.out.println("[DEBUG] new score " + score + ", length increased to " + length);
                }
            }
        }
        setChangedAndNotify();
        return correct;
    }

    // set changed and notify observer
    void setChangedAndNotify() {
        System.out.println("----Model notify");
        setChanged();
        notifyObservers();
    }

    @Override
    public synchronized void deleteObserver(Observer o) {
        System.out.println("----Model deleteObserver: " + o.toString());
        super.deleteObserver(o);
    }

    @Override
    public synchronized void addObserver(Observer o) {
        System.out.println("----Model addObserver: " + o.toString());
        super.addObserver(o);
    }

    @Override
    public synchronized void deleteObservers() {
        System.out.println("----Model delete ALL Observers");
        super.deleteObservers();
    }

    @Override
    public void notifyObservers() {
        System.out.println("----Model notify ALL Observer");
        super.notifyObservers();
    }

};
