package cz.rolling.moirai.exception;

public class WrongPlayersNumberException extends MoiraiException {
    private final int players;
    private final int characters;

    public WrongPlayersNumberException(int players, int characters) {
        super("exception.wrong-number-of-players");
        this.players = players;
        this.characters = characters;
    }

    @Override
    public Object[] getParams() {
        return new Object[]{players, characters};
    }
}
