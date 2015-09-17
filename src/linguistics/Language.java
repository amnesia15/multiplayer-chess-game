package linguistics;

public abstract class Language {
	protected String languageName;
	protected String gameTitleText;
	protected String usernameText;
	protected String passwordText;
	protected String emailText;
	protected String repeatEmailText;
	protected String registerText;
	protected String loginText;
	protected String signUpText;
	protected String wrongPassOrEmailText;
	protected String okText;
	protected String alertText;
	protected String emailsNotEqualText;
	protected String cancelText;
	protected String thankRegisterText;
	protected String settingsText;
	protected String signOutText;
	protected String playAGameWithBotsText;
	protected String onlinePlayersSearchingGameText;
	protected String onlineFriends;
	protected String backText;
	protected String searchText;
	protected String sendText;
	protected String choseAFigureText;
	protected String gameRequestText;
	protected String gameRequestFromText;
	
	protected Language(String languageName) {
		this.languageName = languageName;
	}

	public String getLanguageName() {
		return languageName;
	}

	public String getGameTitleText() {
		return gameTitleText;
	}

	public String getUsernameText() {
		return usernameText;
	}

	public String getPasswordText() {
		return passwordText;
	}

	public String getEmailText() {
		return emailText;
	}

	public String getRepeatEmailText() {
		return repeatEmailText;
	}

	public String getRegisterText() {
		return registerText;
	}

	public String getLoginText() {
		return loginText;
	}

	public String getSignUpText() {
		return signUpText;
	}

	public String getWrongPassOrEmailText() {
		return wrongPassOrEmailText;
	}

	public String getOkText() {
		return okText;
	}

	public String getAlertText() {
		return alertText;
	}

	public String getEmailsNotEqualText() {
		return emailsNotEqualText;
	}

	public String getCancelText() {
		return cancelText;
	}

	public String getThankRegisterText() {
		return thankRegisterText;
	}

	public String getSettingsText() {
		return settingsText;
	}

	public String getSignOutText() {
		return signOutText;
	}

	public String getPlayAGameWithBotsText() {
		return playAGameWithBotsText;
	}

	public String getOnlinePlayersSearchingGameText() {
		return onlinePlayersSearchingGameText;
	}

	public String getOnlineFriends() {
		return onlineFriends;
	}

	public String getBackText() {
		return backText;
	}
	
	public String getSearchText() {
		return searchText;
	}
	
	public String getSendText() {
		return sendText;
	}
	
	public String getChoseAFigureText() {
		return choseAFigureText;
	}
	
	public String getGameRequestText() {
		return gameRequestText;
	}
	
	public String getGameRequestFromText() {
		return gameRequestFromText;
	}
}
