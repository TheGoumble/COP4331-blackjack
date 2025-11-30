package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import util.CardVisuals;
import util.StyleConstants;

/**
 * Tutorial view with slide-based Blackjack rules and instructions
 * 
 * @author Bridjet Walker, Luca Lombardo
 */
public class TutorialView extends BorderPane {

    public final Button exitButton = new Button("Exit Tutorial");
    public final Button nextButton = new Button("Next");
    public final Button prevButton = new Button("Previous");
    
    private final VBox[] slides;
    private int currentSlideIndex = 0;
    private final Label slideIndicator = new Label();

    public TutorialView() {
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #2d5016;");
        
        // Create all slides
        slides = new VBox[]{
            createWelcomeSlide(),
            createBasicRulesSlide(),
            createCardValuesSlide(),
            createGameFlowSlide(),
            createPlayerActionsSlide(),
            createDealerRulesSlide(),
            createPayoutsSlide(),
            createAppInstructionsSlide()
        };

        // Style navigation buttons
        StyleConstants.styleButton(nextButton);
        StyleConstants.styleButton(prevButton);
        StyleConstants.styleButton(exitButton);
        
        // Create navigation bar
        HBox navigationLeft = new HBox(10, prevButton);
        navigationLeft.setAlignment(Pos.CENTER_LEFT);
        
        HBox navigationCenter = new HBox(10, slideIndicator);
        navigationCenter.setAlignment(Pos.CENTER);
        
        HBox navigationRight = new HBox(10, nextButton);
        navigationRight.setAlignment(Pos.CENTER_RIGHT);
        
        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, javafx.scene.layout.Priority.ALWAYS);
        
        HBox navigation = new HBox(navigationLeft, leftSpacer, navigationCenter, rightSpacer, navigationRight);
        navigation.setPadding(new Insets(10, 0, 10, 0));
        
        VBox bottomContainer = new VBox(10, navigation, exitButton);
        bottomContainer.setAlignment(Pos.CENTER);
        
        ScrollPane scrollPane = new ScrollPane(slides[0]);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        setCenter(scrollPane);
        setBottom(bottomContainer);
        
        updateSlideIndicator();
        updateNavigationButtons();
    }

    private VBox createWelcomeSlide() {
        Label title = createTitle("Welcome to Blackjack Tutorial", 32);
        Label subtitle = createSubtitle("Learn the rules and strategies of Blackjack");
        Label body = createBody(
                """
                This tutorial will guide you through:
                
                • Basic rules of Blackjack
                • Card values and scoring
                • How a round works
                • Player actions (Hit/Stand)
                • Dealer rules
                • Payouts and winning conditions
                • Using this application
                
                Click "Next" to begin learning!
                """
        );
        
        return createSlide(Pos.CENTER, title, subtitle, body);
    }

    private VBox createBasicRulesSlide() {
        Label title = createTitle("Basic Rules");
        Label body = createBody(
                """
                The Objective:
                • Get as close to 21 as possible without going over (busting)
                • Beat the dealer's hand
                
                Starting the Game:
                • You begin with a balance
                • Place a bet before each round
                • You and the dealer receive two cards
                • One of the dealer's cards is face down (hidden)
                
                Winning:
                • Have a higher total than the dealer without busting
                • The dealer busts while you don't
                • Get Blackjack (21 with first two cards)
                """
        );
        
        return createSlide(Pos.TOP_LEFT, title, body);
    }

    private VBox createCardValuesSlide() {
        Label title = createTitle("Card Values");
        
        // Number Cards Section
        VBox numberSection = createCardSection(
            "Number Cards (2-10):",
            "Worth their face value",
            createCardExample(CardVisuals.createLargeCardSymbol("♥", "5", "red"), "=", "5 points")
        );
        
        // Face Cards Section
        HBox faceCards = new HBox(10, 
            CardVisuals.createLargeCardSymbol("♠", "K", "black"),
            CardVisuals.createLargeCardSymbol("♥", "Q", "red"),
            CardVisuals.createLargeCardSymbol("♣", "J", "black")
        );
        faceCards.setAlignment(Pos.CENTER);
        VBox faceSection = createCardSection(
            "Face Cards (Jack, Queen, King):",
            "All worth 10 points",
            createCardExample(faceCards, "=", "10 points each")
        );
        
        // Aces Section
        HBox aceCards = new HBox(10,
            CardVisuals.createLargeCardSymbol("♦", "A", "red"),
            createOperatorLabel("+"),
            CardVisuals.createLargeCardSymbol("♠", "9", "black")
        );
        aceCards.setAlignment(Pos.CENTER);
        VBox acesSection = createCardSection(
            "Aces:",
            "Worth either 1 or 11 points (automatically calculated to benefit you)",
            createCardExample(aceCards, "=", "20 points\n(Ace = 11)")
        );
        
        return createSlide(Pos.TOP_LEFT, title, numberSection, faceSection, acesSection);
    }

    private VBox createGameFlowSlide() {
        Label title = createTitle("Flow of a Round");
        
        VBox step1 = createStep("1. Place Your Bet", 
            "Enter the amount you want to wager and click 'Place Bet' to start");
        
        // Step 2 with visual
        HBox yourCards = new HBox(10, 
            CardVisuals.createLargeCardSymbol("♠", "K", "black"),
            CardVisuals.createLargeCardSymbol("♥", "9", "red")
        );
        yourCards.setAlignment(Pos.CENTER);
        
        HBox dealerCards = new HBox(10,
            CardVisuals.createLargeCardSymbol("♦", "7", "red"),
            CardVisuals.createLargeHiddenCard()
        );
        dealerCards.setAlignment(Pos.CENTER);
        
        VBox dealSection = new VBox(8,
            createSectionTitle("👤 You:"),
            yourCards,
            createSectionTitle("🤵 Dealer:"),
            dealerCards
        );
        dealSection.setAlignment(Pos.CENTER);
        dealSection.setPadding(new Insets(10, 0, 10, 0));
        
        VBox step2 = createStep("2. Initial Deal",
            "You receive two cards (both visible), Dealer gets two cards (one hidden)",
            dealSection);
        
        VBox step3 = createStep("3. Your Turn",
            "Decide whether to Hit (take another card) or Stand (end your turn)");
        
        VBox step4 = createStep("4. Dealer's Turn",
            "Dealer reveals hidden card and must draw to at least 17");
        
        VBox step5 = createStep("5. Determine Winner",
            "Compare totals and calculate payout based on who won");
        
        return createSlide(Pos.TOP_LEFT, title, step1, step2, step3, step4, step5);
    }

    private VBox createPlayerActionsSlide() {
        Label title = createTitle("Player Actions");
        Label subtitle = new Label("What You Can Do on Your Turn:");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #555555; -fx-font-family: " + StyleConstants.FONT_ARIAL + ";");
        
        // Hit Section
        Button hitButton = createGameButton("Hit");
        VBox hitSection = createActionSection(
            "Hit:",
            "• Take another card from the deck\n• Use when your total is low and you want to get closer to 21\n• Risk: You might bust if your total goes over 21",
            hitButton
        );
        
        // Stand Section
        Button standButton = createGameButton("Stand");
        VBox standSection = createActionSection(
            "Stand:",
            "• Keep your current hand and end your turn\n• Use when you're satisfied with your total\n• The dealer will then take their turn",
            standButton
        );
        
        // Important Section
        VBox importantSection = createActionSection(
            "Important:",
            "• If you go over 21, you bust immediately and lose your bet\n• The round ends if you bust (dealer doesn't play)\n• Choose wisely based on your cards and the dealer's visible card",
            null
        );
        
        VBox content = new VBox(15, title, subtitle, hitSection, standSection, importantSection);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(40));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        return content;
    }

    private VBox createDealerRulesSlide() {
        Label title = createTitle("Dealer Rules");
        Label body = createBody(
                """
                How the Dealer Plays:
                
                Automatic Rules:
                • The dealer must follow strict rules
                • No decision-making involved
                
                Dealer Must:
                • Hit (draw cards) on totals of 16 or less
                • Stand on totals of 17 or higher
                
                Results:
                • If dealer busts (over 21), you win
                • If dealer's total is higher than yours, you lose
                • If dealer's total is lower than yours, you win
                • If totals are equal, it's a push (tie)
                
                Push (Tie):
                • Your bet is returned to you
                • No money won or lost
                """
        );
        
        return createSlide(Pos.TOP_LEFT, title, body);
    }

    private VBox createPayoutsSlide() {
        Label title = createTitle("Payouts");
        
        // Blackjack Section
        VBox blackjackSection = createPayoutSection(
            "Blackjack (3:2) - Best Payout!",
            "Getting 21 with your first two cards (Ace + 10-value card)",
            CardVisuals.createLargeCardSymbol("♠", "A", "black"),
            CardVisuals.createLargeCardSymbol("♥", "K", "red"),
            "BLACKJACK!\nBet $10 → Win $15"
        );
        
        // Other payout sections
        VBox normalSection = createPayoutInfo("Normal Win (1:1):",
            "Beat the dealer - get your bet back plus the same amount as profit",
            "Bet $10 → Win $10 → Total return: $20");
        
        VBox pushSection = createPayoutInfo("Push (Tie):",
            "Same total as dealer - your bet is returned with no profit or loss",
            "Bet $10 → Get $10 back");
        
        VBox lossSection = createPayoutInfo("Loss:",
            "Dealer beats you or you bust - lose your entire bet",
            "Bet $10 → Lose $10");
        
        return createSlide(Pos.TOP_LEFT, title, blackjackSection, normalSection, pushSection, lossSection);
    }

    private VBox createAppInstructionsSlide() {
        Label title = createTitle("Using This Application");
        Label subtitle = new Label("Understanding the Game Interface:");
        subtitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #555555; -fx-font-family: " + StyleConstants.FONT_ARIAL + ";");
        
        // UI Element sections
        VBox betAmountSection = createUIElement("Bet Amount Field:",
            "• Text field where you enter how much you want to bet\n• Must be within your available balance\n• Required before starting a round",
            null);
        
        Button placeBetBtn = createWhiteButton("Place Bet");
        VBox placeBetSection = createUIElement("Place Bet Button:",
            "• Click this after entering your bet amount\n• Starts the round and deals initial cards\n• Cannot place bet if amount is invalid or exceeds balance",
            placeBetBtn);
        
        Button hitBtn = createGameButton("Hit");
        VBox hitBtnSection = createUIElement("Hit Button:",
            "• Click to receive another card\n• Available during your turn\n• Disabled when it's not your turn or after you stand",
            hitBtn);
        
        Button standBtn = createGameButton("Stand");
        VBox standBtnSection = createUIElement("Stand Button:",
            "• Click to end your turn and keep your current hand\n• Dealer will then reveal their cards and play\n• Cannot be undone once clicked",
            standBtn);
        
        Button startRoundBtn = createWhiteButton("Start Round");
        VBox startRoundSection = createUIElement("Start Round Button:",
            "• Appears after a round ends\n• Click to begin a new round\n• Allows you to place a new bet",
            startRoundBtn);
        
        // Flow summary
        Label flowTitle = createSectionTitle("Typical Game Flow:");
        Label flowDesc = new Label("1. Enter bet amount → 2. Place Bet → 3. Cards dealt → 4. Hit/Stand → 5. Round ends → 6. Start Round (repeat)");
        flowDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-family: " + StyleConstants.FONT_ARIAL + "; -fx-font-style: italic;");
        flowDesc.setWrapText(true);
        VBox flowSection = new VBox(5, flowTitle, flowDesc);
        
        VBox content = new VBox(18, title, subtitle, betAmountSection, placeBetSection, hitBtnSection, standBtnSection, startRoundSection, flowSection);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(40));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        return content;
    }

    public void nextSlide() {
        if (currentSlideIndex < slides.length - 1) {
            currentSlideIndex++;
            ScrollPane scrollPane = new ScrollPane(slides[currentSlideIndex]);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setStyle("-fx-background-color: transparent;");
            setCenter(scrollPane);
            updateSlideIndicator();
            updateNavigationButtons();
        }
    }

    public void previousSlide() {
        if (currentSlideIndex > 0) {
            currentSlideIndex--;
            ScrollPane scrollPane = new ScrollPane(slides[currentSlideIndex]);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setStyle("-fx-background-color: transparent;");
            setCenter(scrollPane);
            updateSlideIndicator();
            updateNavigationButtons();
        }
    }

    private void updateSlideIndicator() {
        slideIndicator.setText((currentSlideIndex + 1) + " / " + slides.length);
        slideIndicator.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: 'Arial';");
    }

    private void updateNavigationButtons() {
        prevButton.setDisable(currentSlideIndex == 0);
        nextButton.setDisable(currentSlideIndex == slides.length - 1);
    }

    // ===== Helper Methods =====
    
    private Label createTitle(String text) {
        return createTitle(text, 28);
    }
    
    private Label createTitle(String text, int fontSize) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: " + fontSize + "px; -fx-font-weight: bold; -fx-text-fill: " + StyleConstants.GREEN_FELT + "; -fx-font-family: " + StyleConstants.FONT_ARIAL + ";");
        return label;
    }
    
    private Label createSubtitle(String text) {
        Label label = new Label(text);
        label.setStyle(StyleConstants.SUBTITLE_TEXT);
        return label;
    }
    
    private Label createSectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + StyleConstants.GREEN_FELT + "; -fx-font-family: " + StyleConstants.FONT_ARIAL + ";");
        return label;
    }
    
    private Label createBody(String text) {
        Label label = new Label(text);
        label.setStyle(StyleConstants.BODY_TEXT);
        label.setWrapText(true);
        return label;
    }
    
    private VBox createSlide(Pos alignment, javafx.scene.Node... children) {
        VBox content = new VBox(20, children);
        content.setAlignment(alignment);
        content.setPadding(new Insets(40));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        return content;
    }
    
    private VBox createStep(String title, String description) {
        Label titleLabel = createSectionTitle(title);
        Label descLabel = createBody(description);
        return new VBox(5, titleLabel, descLabel);
    }
    
    private VBox createStep(String title, String description, javafx.scene.Node visual) {
        Label titleLabel = createSectionTitle(title);
        Label descLabel = createBody(description);
        return new VBox(5, titleLabel, descLabel, visual);
    }
    
    private VBox createCardSection(String title, String description, HBox example) {
        Label titleLabel = createSectionTitle(title);
        Label descLabel = createBody(description);
        VBox section = new VBox(10, titleLabel, descLabel, example);
        section.setAlignment(Pos.CENTER_LEFT);
        return section;
    }
    
    private HBox createCardExample(javafx.scene.Node cards, String operator, String result) {
        Label opLabel = createOperatorLabel(operator);
        Label resultLabel = new Label(result);
        resultLabel.setStyle("-fx-font-size: 16px;");
        resultLabel.setTextAlignment(TextAlignment.CENTER);
        
        HBox example = new HBox(15, cards, opLabel, resultLabel);
        example.setAlignment(Pos.CENTER);
        return example;
    }
    
    private Label createOperatorLabel(String operator) {
        Label label = new Label(operator);
        label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + StyleConstants.GREEN_FELT + ";");
        return label;
    }
    
    private Button createGameButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + StyleConstants.GREEN_FELT + "; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 8 25 8 25; " +
            "-fx-background-radius: 5; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        );
        button.setOnAction(e -> {});
        return button;
    }
    
    private VBox createActionSection(String title, String description, Button button) {
        Label titleLabel = createSectionTitle(title);
        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-family: " + StyleConstants.FONT_ARIAL + ";");
        descLabel.setWrapText(true);
        
        if (button != null) {
            HBox buttonBox = new HBox(button);
            buttonBox.setAlignment(Pos.CENTER_LEFT);
            buttonBox.setPadding(new Insets(8, 0, 0, 0));
            return new VBox(5, titleLabel, descLabel, buttonBox);
        }
        return new VBox(5, titleLabel, descLabel);
    }
    
    private VBox createPayoutSection(String title, String desc, VBox card1, VBox card2, String result) {
        Label titleLabel = createSectionTitle(title);
        Label descLabel = createBody(desc);
        
        HBox cards = new HBox(10, card1, createOperatorLabel("+"), card2);
        cards.setAlignment(Pos.CENTER);
        
        HBox example = new HBox(15, cards, createOperatorLabel("="), createBlackjackLabel(result));
        example.setAlignment(Pos.CENTER);
        
        VBox section = new VBox(10, titleLabel, descLabel, example);
        section.setAlignment(Pos.CENTER_LEFT);
        return section;
    }
    
    private VBox createPayoutInfo(String title, String desc, String example) {
        Label titleLabel = createSectionTitle(title);
        Label descLabel = createBody(desc);
        Label exampleLabel = new Label(example);
        exampleLabel.setStyle("-fx-font-size: 15px; -fx-padding: 10 0 0 20; -fx-text-fill: #333333; -fx-font-family: " + StyleConstants.FONT_ARIAL + ";");
        
        VBox section = new VBox(8, titleLabel, descLabel, exampleLabel);
        section.setAlignment(Pos.CENTER_LEFT);
        return section;
    }
    
    private Button createWhiteButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: white; " +
            "-fx-text-fill: black; " +
            "-fx-padding: 8 25 8 25; " +
            "-fx-background-radius: 5; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        );
        button.setOnAction(e -> {});
        return button;
    }
    
    private VBox createUIElement(String title, String description, Button button) {
        Label titleLabel = createSectionTitle(title);
        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-family: " + StyleConstants.FONT_ARIAL + ";");
        descLabel.setWrapText(true);
        
        if (button != null) {
            HBox buttonBox = new HBox(button);
            buttonBox.setPadding(new Insets(8, 0, 0, 0));
            return new VBox(5, titleLabel, descLabel, buttonBox);
        }
        return new VBox(5, titleLabel, descLabel);
    }
    
    private Label createBlackjackLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + StyleConstants.GREEN_FELT + "; -fx-font-family: " + StyleConstants.FONT_ARIAL + ";");
        label.setTextAlignment(TextAlignment.CENTER);
        return label;
    }
}