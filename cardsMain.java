import java.util.*;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.io.BufferedReader;

/**
 * cards
 */

class reader {

    HashMap<String, Integer> cardHashMap = new HashMap<>();

    String filepath = "cards.txt";

    boolean read() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            String[] record;

            while ((line = reader.readLine()) != null) {
                if (line.contains(":")) {
                    record = line.split(":", 2);
                    String cardName = record[0].trim();
                    int cardValue = Integer.parseInt(record[1].trim());
                    cardHashMap.put(cardName, cardValue);
                    // System.out.print(record[0] + ":" + record[1] + "\n");
                }
            }
            reader.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    HashMap<String, Integer> getCardMap() {
        read();
        return cardHashMap;
    }

}

class cards {

    String[] cardDeskStack = new String[52];
    HashMap<String, Integer> cardHashMap = new HashMap<>();

    void createDeck() {
        List<String> deck = new ArrayList<>();
        for (String suit : new String[] { "a", "b", "c", "d" })
            for (int rank = 1; rank <= 13; rank++)
                deck.add(suit + rank);
        Collections.shuffle(deck);
        cardDeskStack = deck.toArray(String[]::new);
        intiateCards();
        // System.out.println(a[1][card]);

    }

    // System.out.print(cardDeck);
    String getCardEmoji(String card) {
        char suit = card.charAt(0);
        String rank = card.substring(1);

        String symbol = switch (suit) {
            case 'a' -> "♠";
            case 'b' -> "♥";
            case 'c' -> "♦";
            case 'd' -> "♣";
            default -> "?";
        };
        System.out.println("card: " + card);
        System.out.println("suit: " + suit);
        System.out.println("symbol: " + symbol);
        String value = switch (rank) {
            case "1" -> "A";
            case "11" -> "J";
            case "12" -> "Q";
            case "13" -> "K";
            default -> rank;
        };

        return symbol + " " + value;
    }

    void intiateCards() {
        reader fileReader = new reader();
        this.cardHashMap = fileReader.getCardMap();
    }

    HashMap<String, Integer> getCardHashMap() {
        return cardHashMap;
    }

    String[] getCardDeck() {
        createDeck();
        return cardDeskStack;
    }

    void shuffledeck() {
        this.createDeck();
    }

}

/**
 * player
 */
class player {
    String[] cards = new String[5];
    int Cardcount = 0;
    String name;
    cards cardsInstance = new cards();

    player(String name) {
        this.name = name;
    }

    boolean assignedCard(String card) {

        cards[Cardcount] = card;
        Cardcount++;
        return true;
    }

    String ThrowCard(int cardIdx, String pickupCard) {
        String thrownCard = cards[cardIdx - 1];
        System.out.print("thrown card : " + cardsInstance.getCardEmoji(cards[cardIdx - 1]));
        cards[cardIdx - 1] = pickupCard;
        return thrownCard;
    }

    void displayCards() {
        System.out.print(Arrays.deepToString(cards));
    }

    String playerActions(Scanner sc, ArrayList<String> cardsOnTable) {
        int playerSelectedNo = Integer.MAX_VALUE;
        System.out.print("\nEnter Arg");
        System.out.print(">> ");
        try {
            playerSelectedNo = Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.ThrowCard(playerSelectedNo, cardsOnTable.getLast());
    }

    String playerActions2(Scanner sc, ArrayList<String> thrownCardStack) {
        int playerSelectedNo = Integer.MAX_VALUE;
        System.out.print("\nEnter Arg");
        System.out.print(">> ");
        try {
            playerSelectedNo = Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.ThrowCard(playerSelectedNo, thrownCardStack.getLast());
    }

}

class game {

    private player a, b;
    private cards cards;
    private Scanner sc;
    private Panel GamePanel;
    private ArrayList<String> cardsOnTable = new ArrayList<>();
    private ArrayList<String> remaingCardsOndeck = new ArrayList<>();
    private ArrayList<String> thrownCardStack = new ArrayList<>();
    private HashMap<String, Integer> cardHashMap = new HashMap<>();

    private int cardIndexInStack = 0;

    game(Scanner sc, Panel GamePanel) {
        this.a = new player("vish");
        this.b = new player("vickz");
        this.cards = new cards();
        this.sc = sc;
        this.GamePanel = GamePanel;
    }

    boolean winnerCheck(player p, String ExtraCard) {
        int[] cardNum = new int[p.cards.length];
        for (int i = 0; i < p.cards.length; i++) {
            cardNum[i] = cardHashMap.get(p.cards[i]);
        }

        HashSet<Integer> set = new HashSet<>();
        for (int i : cardNum) {
            int num = i % 100;
            boolean check = set.add(num);
            if (!check) {
                set.remove(num);
            }

        }
        int lastCard = cardHashMap.get(ExtraCard) % 100;
        boolean check = set.add(lastCard);
        if (!check) {
            set.remove(lastCard);
        }

        if (set.isEmpty()) {
            System.out.println("Matched");
            return true;
        }
        return false;

    }

    void distributeCard() {

        String[] cardStack = cards.getCardDeck();
        cardHashMap = cards.getCardHashMap();
        boolean fistPlayer = true;
        cardIndexInStack = 0;

        while (a.Cardcount < 5 || b.Cardcount < 5) {

            if (fistPlayer) {
                a.assignedCard(cardStack[cardIndexInStack]);

            } else {
                b.assignedCard(cardStack[cardIndexInStack]);
            }
            fistPlayer = !fistPlayer;
            cardIndexInStack++;
        }
        for (int i = cardIndexInStack; i < cardStack.length; i++) {
            remaingCardsOndeck.add(cardStack[i]);
        }

    }

    void start() {

        distributeCard();

        int arg = -1;
        boolean winnerFound = false;
        boolean fistPlayerturn = true;

        // ====================
        while (arg != 0 && !winnerFound) {
            cardsOnTable.add(remaingCardsOndeck.getLast());
            if (arg == -1) {
                remaingCardsOndeck.removeLast();
                GamePanel.GameStatedMsg();
                GamePanel.defaultNewGameMenu();
                thrownCardStack.add(remaingCardsOndeck.getLast());
            }

            arg = -2;
            GamePanel.printGameStates(this.cardsOnTable.getLast(), this.thrownCardStack.getLast());

            if (fistPlayerturn) {
                GamePanel.printPlayerOption(a.name);
                GamePanel.printPlayerGameState(a.cards);

            } else {
                GamePanel.printPlayerOption(b.name);
                GamePanel.printPlayerGameState(b.cards);

            }
            System.out.print("\nEnter Arg");
            System.out.print(">> ");
            try {
                arg = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                e.printStackTrace();
            }
            String thrownCard;

            // ============================

            switch (arg) {
                case 1:
                    winnerFound = winnerCheck(a, cardsOnTable.getLast());
                    if (winnerFound)
                        break;
                    if (fistPlayerturn) {
                        GamePanel.printPlayerThrowOptMsg(a.cards);
                        thrownCard = a.playerActions(sc, cardsOnTable);
                    } else {
                        GamePanel.printPlayerThrowOptMsg(b.cards);
                        thrownCard = b.playerActions(sc, cardsOnTable);
                    }
                    remaingCardsOndeck.removeLast();

                    thrownCardStack.add(thrownCard);

                    fistPlayerturn = !fistPlayerturn;
                    break;
                // ============================
                case 2:
                    if (winnerFound)
                        break;
                    if (fistPlayerturn) {
                        GamePanel.printPlayerThrowOptMsg(a.cards);
                        thrownCard = a.playerActions2(sc, thrownCardStack);
                    } else {
                        GamePanel.printPlayerThrowOptMsg(b.cards);
                        thrownCard = b.playerActions2(sc, thrownCardStack);
                    }

                    thrownCardStack.add(thrownCard);
                    fistPlayerturn = !fistPlayerturn;
                    break;

                // ============================

                case 9:
                    GamePanel.NewGameMenu();
                    break;
                default:
                    break;
            }

        }

    }

}

/**
 * Panel
 */
class Panel {

    void StartGamePanel() {
        System.out.println("\n===========");
        System.out.println("Select Options");
        System.out.println("1 : New Game");
        System.out.println("2 : Options ");
        System.out.println("0 : Quit");
    }

    void GameStatedMsg() {
        System.out.println("\n===========");
        System.out.println("Game stated!!");
    }

    void NewGameMenu() {
        System.out.println("\n===========");
        System.out.println("Select Options");
        System.out.println("1 : Show Players Cards");
        System.out.println("2 : Show Deck ");
        System.out.println("0 : Back To Menu");
    }

    void defaultNewGameMenu() {
        System.out.println("\n===========");
        System.out.println("Card are distrubuted");
        System.out.println("Press 9 For Open Menu");
    }

    void printPlayerOption(String playerName) {
        System.out.println("\n===========");
        System.out.println(playerName + "'s Turn");
        System.out.println("Select Options");
        System.out.println("1 : Pick Card from Deck");
        System.out.println("2 : Pick Card from table ");
        System.out.println("0 : Back To Menu");
    }

    void printGameStates(String card, String thrownCard) {
        System.out.println("\n===========");
        System.out.println("Card on table : " + card);
        System.out.println("Thrown card on table : " + thrownCard);

    }

    void printPlayerGameState(String[] cards) {
        System.out.println("\n===========");
        System.out.println(Arrays.deepToString(cards));
    }

    void printPlayerThrowOptMsg(String[] cards) {
        System.out.println("\n===========");
        System.out.println("Your Cards : " + Arrays.deepToString(cards));
        System.out.println("Which Card you want to Throw");

    }

}

public class cardsMain {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Panel GamePanel = new Panel();
        int arg = Integer.MAX_VALUE;
        while (arg != 0) {
            GamePanel.StartGamePanel();
            System.out.print("\nEnter Arg");
            System.out.print(">> ");
            try {
                arg = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                e.printStackTrace();
            }
            switch (arg) {
                case 1:
                    game gameInstance = new game(sc, GamePanel);
                    gameInstance.start();
                    break;
                default:
                    break;
            }

        }
        sc.close();
    }
}