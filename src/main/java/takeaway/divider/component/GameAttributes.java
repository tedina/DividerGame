package takeaway.divider.component;

import java.util.List;

/**
 * Created by Teodora.Toncheva on 03.07.2021
 */
public class GameAttributes {
    private Integer result;
    private Integer number;
    private Integer divider;
    private List<Integer> numbers;
    private boolean hasTwoPlayers;

    public GameAttributes() {
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getDivider() {
        return divider;
    }

    public void setDivider(Integer divider) {
        this.divider = divider;
    }

    public List<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<Integer> numbers) {
        this.numbers = numbers;
    }

    public boolean isHasTwoPlayers() {
        return hasTwoPlayers;
    }

    public void setHasTwoPlayers(boolean hasTwoPlayers) {
        this.hasTwoPlayers = hasTwoPlayers;
    }
}
