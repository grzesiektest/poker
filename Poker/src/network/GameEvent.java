package network;

public class GameEvent {

	// stałe eventType
	// C_* (Client) - zdarzenia wysyłane przez klienta
	// S_* (Server) - zdarzenia wysyłane przez serwer
	// SB_* (Server broadcast) - zdarzenia wysyłane przez serwer do wszystkich
	// klientów

	/** Próba zalogowania przez klienta */
	public static final int C_LOGIN = 1001;

	/** Logowanie nie powiodło się */
	public static final int S_LOGIN_FAIL = 1002;

	/** Logowanie powiodło się - wysyłanie informacji do wszystkich graczy */
	public static final int SB_LOGIN = 1003;

	/** Próba wylogowania się przez klienta */
	public static final int C_LOGOUT = 1004;

	/** Wylogowanie klienta - wysyłanie informacji do wszytkich graczy */
	public static final int SB_LOGOUT = 1005;

	/** W grze znajduje sie juz maksymalna liczba graczy */
	public static final int S_TOO_MANY_CONNECTIONS = 1006;
	
	/** W grze znajduje sie juz osoba o takim ID */
	public static final int S_USER_EXIST = 1007;

	/** Wysłanie informacji do wszytkich graczy, ze mozna dolaczyc sie do gry */
	public static final int SB_CAN_JOIN_GAME = 1101;

	/** Wysłanie informacji do wszytkich graczy, ze nie mozna dolaczyc sie do gry */
	public static final int SB_CANNOT_JOIN_GAME = 1102;

	/** Próba dołączenia do gry przez klienta */
	public static final int C_JOIN_GAME = 1103;

	/** Klient dołączył do gry */
	public static final int S_JOIN_GAME_OK = 1104;

	/** Klient nie dołączył do gry */
	public static final int S_JOIN_GAME_FAIL = 1105;

	/** Klient dołączył do gry - wysyłanie informacji do wszystkich graczy */
	public static final int SB_PLAYER_JOINED = 1106;

	/** Wysyłanie informacji do wszystkich graczy o rozpoczeciu gry */
	public static final int SB_START_GAME = 1107;

	/** Próba zakonczenia gry przez gracza */
	public static final int C_QUIT_GAME = 1108;

	/** Klient zakończył grę - wysyłanie informacji do wszytkich graczy */
	public static final int SB_PLAYER_QUIT = 1109;

	/** Wszystkie grafiki załadowane - gracz gotowy do gry */
	public static final int C_READY = 1110;

	/** Wszyscy gracze gotowi do gry */
	public static final int SB_ALL_READY = 1111;

	/** Klient wysyła wiadomość tekstową */
	public static final int C_CHAT_MSG = 1201;

	/** Swerwer przesyła wiadomość tekstową do wszystkich graczy */
	public static final int SB_CHAT_MSG = 1202;

	/** Gracz strzela do przeciwnika */
	public static final int C_SHOT = 1301;

	/** Serwer przesyla informacje o strzale gracza */
	public static final int SB_SHOT = 1302;

	/** Wynik Strzału */
	public static final int C_SHOT_RESULT = 1304;

	/** Serwer rozszyła wynik strzału */
	public static final int SB_SHOT_RESULT = 1305;	
	
	/** */
	public static final int C_DEAD = 1308;

	/** */
	public static final int SB_DEAD = 1309;

	/**  */
	public static final int C_PLAYER_DEAD = 1310;

	/**  */
	public static final int SB_GAME_OVER = 1311;

	// -----------------------------------------------------

	/** Typ zdarzenia */
	private int eventType;

	/** ID gracza który przesłał wiadomość */
	private String playerId = "";

	/** treść wiadomości */
	private String message;

	public GameEvent() {

	}

	public GameEvent(int type) {
		setType(type);
	}

	public GameEvent(int type, String message) {
		this(type);
		this.message = message;
	}

	public GameEvent(String receivedMessage) {
		String x = receivedMessage;
		int idx1 = x.indexOf('|');
		int idx2 = x.indexOf('|', idx1 + 1);
		String a = x.substring(0, idx1);
		String b = x.substring(idx1 + 1, idx2);
		String c = x.substring(idx2 + 1);
		try {
			setType(Integer.parseInt(a));
		} catch (NumberFormatException ex) {
			setType(-1);
		}
		setPlayerId(b);
		setMessage(c);
	}

	public String toSend() {
		String toSend = eventType + "|" + playerId + "|" + getMessage();
		return toSend;
	}

	public void setType(int type) {
		eventType = type;
	}

	public int getType() {
		return eventType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String id) {
		playerId = id;
	}
}
