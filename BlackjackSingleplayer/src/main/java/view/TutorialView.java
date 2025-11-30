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
/**
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
        nextButton.setStyle("-fx-font-size: 14px; -fx-background-color: white; -fx-text-fill: #2d5016; -fx-font-weight: bold;");
        prevButton.setStyle("-fx-font-size: 14px; -fx-background-color: white; -fx-text-fill: #2d5016; -fx-font-weight: bold;");
        exitButton.setStyle("-fx-font-size: 14px; -fx-background-color: white; -fx-text-fill: #2d5016; -fx-font-weight: bold;");
        
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
        Label title = new Label("Welcome to Blackjack Tutorial");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        
        Label subtitle = new Label("Learn the rules and strategies of Blackjack");
        subtitle.setStyle("-fx-font-size: 18px; -fx-text-fill: #555555; -fx-font-family: 'Arial';");
        
        Label body = new Label(
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
        body.setWrapText(true);
        body.setStyle("-fx-font-size: 15px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        
        VBox content = new VBox(20, title, subtitle, body);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        return content;
    }

    private VBox createBasicRulesSlide() {
        Label title = new Label("Basic Rules");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        
        Label body = new Label(
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
        body.setWrapText(true);
        body.setStyle("-fx-font-size: 15px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        
        VBox content = new VBox(20, title, body);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(40));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        return content;
    }

    private VBox createCardValuesSlide() {
        Label title = new Label("Card Values");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        
        // Number Cards Section
        Label numberCardsTitle = new Label("Number Cards (2-10):");
        numberCardsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        
        Label numberCardsDesc = new Label("Worth their face value");
        numberCardsDesc.setStyle("-fx-font-size: 15px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        
        // Visual example: 5 of Hearts
        HBox numberCardsExample = new HBox(15);
        numberCardsExample.setAlignment(Pos.CENTER);
        VBox card5 = createCardSymbol("♥", "5", "red");
        Label equalsLabel1 = new Label("=");
        equalsLabel1.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label pointsLabel1 = new Label("5 points");
        pointsLabel1.setStyle("-fx-font-size: 16px;");
        numberCardsExample.getChildren().addAll(card5, equalsLabel1, pointsLabel1);
        
        VBox numberSection = new VBox(10, numberCardsTitle, numberCardsDesc, numberCardsExample);
        numberSection.setAlignment(Pos.CENTER_LEFT);
        
        // Face Cards Section
        Label faceCardsTitle = new Label("Face Cards (Jack, Queen, King):");
        faceCardsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        
        Label faceCardsDesc = new Label("All worth 10 points");
        faceCardsDesc.setStyle("-fx-font-size: 15px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        
        // Visual example: King, Queen, Jack
        HBox faceCardsExample = new HBox(15);
        faceCardsExample.setAlignment(Pos.CENTER);
        VBox cardK = createCardSymbol("♠", "K", "black");
        VBox cardQ = createCardSymbol("♥", "Q", "red");
        VBox cardJ = createCardSymbol("♣", "J", "black");
        Label equalsLabel2 = new Label("=");
        equalsLabel2.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label pointsLabel2 = new Label("10 points each");
        pointsLabel2.setStyle("-fx-font-size: 16px;");
        faceCardsExample.getChildren().addAll(cardK, cardQ, cardJ, equalsLabel2, pointsLabel2);
        
        VBox faceSection = new VBox(10, faceCardsTitle, faceCardsDesc, faceCardsExample);
        faceSection.setAlignment(Pos.CENTER_LEFT);
        
        // Aces Section
        Label acesTitle = new Label("Aces:");
        acesTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        
        Label acesDesc = new Label("Worth either 1 or 11 points (automatically calculated to benefit you)");
        acesDesc.setStyle("-fx-font-size: 15px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        acesDesc.setWrapText(true);
        
        // Visual example: Ace + 9
        HBox acesExample = new HBox(15);
        acesExample.setAlignment(Pos.CENTER);
        VBox cardA = createCardSymbol("♦", "A", "red");
        Label plusLabel = new Label("+");
        plusLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        VBox card9 = createCardSymbol("♠", "9", "black");
        Label equalsLabel3 = new Label("=");
        equalsLabel3.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label pointsLabel3 = new Label("20 points\n(Ace = 11)");
        pointsLabel3.setStyle("-fx-font-size: 16px;");
        pointsLabel3.setTextAlignment(TextAlignment.CENTER);
        acesExample.getChildren().addAll(cardA, plusLabel, card9, equalsLabel3, pointsLabel3);
        
        VBox acesSection = new VBox(10, acesTitle, acesDesc, acesExample);
        acesSection.setAlignment(Pos.CENTER_LEFT);
        
        VBox content = new VBox(25, title, numberSection, faceSection, acesSection);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(40));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        return content;
    }

    private VBox createGameFlowSlide() {
        Label title = new Label("Flow of a Round");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        
        // Step 1: Place Bet
        Label step1Title = new Label("1. Place Your Bet");
        step1Title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        Label step1Desc = new Label("Enter the amount you want to wager and click 'Place Bet' to start");
        step1Desc.setStyle("-fx-font-size: 15px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        step1Desc.setWrapText(true);
        VBox step1 = new VBox(5, step1Title, step1Desc);
        
        // Step 2: Initial Deal with visual example
        Label step2Title = new Label("2. Initial Deal");
        step2Title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        Label step2Desc = new Label("You receive two cards (both visible), Dealer gets two cards (one hidden)");
        step2Desc.setStyle("-fx-font-size: 15px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        step2Desc.setWrapText(true);
        
        // Visual example of initial deal
        Label youLabel = new Label("👤 You:");
        youLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        HBox yourCards = new HBox(10);
        yourCards.setAlignment(Pos.CENTER);
        VBox yourCard1 = createCardSymbol("♠", "K", "black");
        VBox yourCard2 = createCardSymbol("♥", "9", "red");
        yourCards.getChildren().addAll(yourCard1, yourCard2);
        
        Label dealerLabel = new Label("🤵 Dealer:");
        dealerLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        HBox dealerCards = new HBox(10);
        dealerCards.setAlignment(Pos.CENTER);
        VBox dealerCard1 = createCardSymbol("♦", "7", "red");
        Label hiddenCard = new Label("?");
        hiddenCard.setStyle(
            "-fx-font-size: 36px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-border-color: #333333; " +
            "-fx-border-width: 3px; " +
            "-fx-border-radius: 8; " +
            "-fx-background-color: #4169E1; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 20; " +
            "-fx-min-width: 90px; " +
            "-fx-min-height: 120px;"
        );
        hiddenCard.setAlignment(Pos.CENTER);
        VBox hiddenCardBox = new VBox(hiddenCard);
        hiddenCardBox.setAlignment(Pos.CENTER);
        dealerCards.getChildren().addAll(dealerCard1, hiddenCardBox);
        
        VBox dealSection = new VBox(8, youLabel, yourCards, dealerLabel, dealerCards);
        dealSection.setAlignment(Pos.CENTER);
        dealSection.setPadding(new Insets(10, 0, 10, 0));
        
        VBox step2 = new VBox(5, step2Title, step2Desc, dealSection);
        
        // Step 3: Your Turn
        Label step3Title = new Label("3. Your Turn");
        step3Title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        Label step3Desc = new Label("Decide whether to Hit (take another card) or Stand (end your turn)");
        step3Desc.setStyle("-fx-font-size: 15px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        step3Desc.setWrapText(true);
        VBox step3 = new VBox(5, step3Title, step3Desc);
        
        // Step 4: Dealer's Turn
        Label step4Title = new Label("4. Dealer's Turn");
        step4Title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        Label step4Desc = new Label("Dealer reveals hidden card and must draw to at least 17");
        step4Desc.setStyle("-fx-font-size: 15px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        step4Desc.setWrapText(true);
        VBox step4 = new VBox(5, step4Title, step4Desc);
        
        // Step 5: Winner
        Label step5Title = new Label("5. Determine Winner");
        step5Title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        Label step5Desc = new Label("Compare totals and calculate payout based on who won");
        step5Desc.setStyle("-fx-font-size: 15px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        step5Desc.setWrapText(true);
        VBox step5 = new VBox(5, step5Title, step5Desc);
        
        VBox content = new VBox(18, title, step1, step2, step3, step4, step5);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(40));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        return content;
    }

    private VBox createPlayerActionsSlide() {
        Label title = new Label("Player Actions");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        
        Label subtitle = new Label("What You Can Do on Your Turn:");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #555555; -fx-font-family: 'Arial';");
        
        // Hit Section
        Label hitTitle = new Label("Hit:");
        hitTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        
        Label hitDesc = new Label(
                "• Take another card from the deck\n" +
                "• Use when your total is low and you want to get closer to 21\n" +
                "• Risk: You might bust if your total goes over 21"
        );
        hitDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        hitDesc.setWrapText(true);
        
        // Visual Hit Button Example
        Button hitButtonExample = new Button("Hit");
        hitButtonExample.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: #2d5016; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 8 25 8 25; " +
            "-fx-background-radius: 5; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        );
        hitButtonExample.setOnAction(e -> {}); // No action, just for display
        HBox hitButtonBox = new HBox(hitButtonExample);
        hitButtonBox.setAlignment(Pos.CENTER_LEFT);
        hitButtonBox.setPadding(new Insets(8, 0, 0, 0));
        
        VBox hitSection = new VBox(5, hitTitle, hitDesc, hitButtonBox);
        
        // Stand Section
        Label standTitle = new Label("Stand:");
        standTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        
        Label standDesc = new Label(
                "• Keep your current hand and end your turn\n" +
                "• Use when you're satisfied with your total\n" +
                "• The dealer will then take their turn"
        );
        standDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        standDesc.setWrapText(true);
        
        // Visual Stand Button Example
        Button standButtonExample = new Button("Stand");
        standButtonExample.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: #2d5016; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 8 25 8 25; " +
            "-fx-background-radius: 5; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        );
        standButtonExample.setOnAction(e -> {}); // No action, just for display
        HBox standButtonBox = new HBox(standButtonExample);
        standButtonBox.setAlignment(Pos.CENTER_LEFT);
        standButtonBox.setPadding(new Insets(8, 0, 0, 0));
        
        VBox standSection = new VBox(5, standTitle, standDesc, standButtonBox);
        
        // Important Section
        Label importantTitle = new Label("Important:");
        importantTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        
        Label importantDesc = new Label(
                "• If you go over 21, you bust immediately and lose your bet\n" +
                "• The round ends if you bust (dealer doesn't play)\n" +
                "• Choose wisely based on your cards and the dealer's visible card"
        );
        importantDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        importantDesc.setWrapText(true);
        
        VBox importantSection = new VBox(5, importantTitle, importantDesc);
        
        VBox content = new VBox(15, title, subtitle, hitSection, standSection, importantSection);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(40));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        return content;
    }

    private VBox createDealerRulesSlide() {
        Label title = new Label("🤵 Dealer Rules");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        
        Label body = new Label(
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
        body.setWrapText(true);
        body.setStyle("-fx-font-size: 15px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        
        VBox content = new VBox(20, title, body);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(40));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        return content;
    }

    private VBox createPayoutsSlide() {
        Label title = new Label("Payouts");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        
        // Blackjack Example (Best payout!)
        Label blackjackTitle = new Label("Blackjack (3:2) - Best Payout!");
        blackjackTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        
        Label blackjackDesc = new Label("Getting 21 with your first two cards (Ace + 10-value card)");
        blackjackDesc.setStyle("-fx-font-size: 15px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        
        HBox blackjackExample = new HBox(15);
        blackjackExample.setAlignment(Pos.CENTER);
        VBox cardA = createCardSymbol("♠", "A", "black");
        Label plusLabel1 = new Label("+");
        plusLabel1.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2d5016;");
        VBox cardK = createCardSymbol("♥", "K", "red");
        Label equalsLabel1 = new Label("=");
        equalsLabel1.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2d5016;");
        Label blackjackLabel = new Label("BLACKJACK!\nBet $10 → Win $15");
        blackjackLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        blackjackLabel.setTextAlignment(TextAlignment.CENTER);
        blackjackExample.getChildren().addAll(cardA, plusLabel1, cardK, equalsLabel1, blackjackLabel);
        
        VBox blackjackSection = new VBox(10, blackjackTitle, blackjackDesc, blackjackExample);
        blackjackSection.setAlignment(Pos.CENTER_LEFT);
        
        // Normal Win Example
        Label normalTitle = new Label("Normal Win (1:1):");
        normalTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        
        Label normalDesc = new Label("Beat the dealer - get your bet back plus the same amount as profit");
        normalDesc.setStyle("-fx-font-size: 15px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        
        Label normalExample = new Label("Bet $10 → Win $10 → Total return: $20");
        normalExample.setStyle("-fx-font-size: 15px; -fx-padding: 10 0 0 20; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        
        VBox normalSection = new VBox(8, normalTitle, normalDesc, normalExample);
        normalSection.setAlignment(Pos.CENTER_LEFT);
        
        // Push Example
        Label pushTitle = new Label("Push (Tie):");
        pushTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        
        Label pushDesc = new Label("Same total as dealer - your bet is returned with no profit or loss");
        pushDesc.setStyle("-fx-font-size: 15px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        
        Label pushExample = new Label("Bet $10 → Get $10 back");
        pushExample.setStyle("-fx-font-size: 15px; -fx-padding: 10 0 0 20; -fx-text-fill: #333333; -fx-font-family: 'Arial';");

        VBox pushSection = new VBox(8, pushTitle, pushDesc, pushExample);
        pushSection.setAlignment(Pos.CENTER_LEFT);
        
        // Loss Example
        Label lossTitle = new Label("Loss:");
        lossTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        
        Label lossDesc = new Label("Dealer beats you or you bust - lose your entire bet");
        lossDesc.setStyle("-fx-font-size: 15px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        
        Label lossExample = new Label("Bet $10 → Lose $10");
        lossExample.setStyle("-fx-font-size: 15px; -fx-padding: 10 0 0 20; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        
        VBox lossSection = new VBox(8, lossTitle, lossDesc, lossExample);
        lossSection.setAlignment(Pos.CENTER_LEFT);
        
        VBox content = new VBox(20, title, blackjackSection, normalSection, pushSection, lossSection);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(40));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        return content;
    }

    private VBox createAppInstructionsSlide() {
        Label title = new Label("Using This Application");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        
        Label subtitle = new Label("Understanding the Game Interface:");
        subtitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #555555; -fx-font-family: 'Arial';");
        
        // Bet Amount Field
        Label betAmountTitle = new Label("Bet Amount Field:");
        betAmountTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        Label betAmountDesc = new Label("• Text field where you enter how much you want to bet\n• Must be within your available balance\n• Required before starting a round");
        betAmountDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        betAmountDesc.setWrapText(true);
        VBox betAmountSection = new VBox(5, betAmountTitle, betAmountDesc);
        
        // Place Bet Button
        Label placeBetTitle = new Label("Place Bet Button:");
        placeBetTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        Label placeBetDesc = new Label("• Click this after entering your bet amount\n• Starts the round and deals initial cards\n• Cannot place bet if amount is invalid or exceeds balance");
        placeBetDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        placeBetDesc.setWrapText(true);
        Button placeBetExample = new Button("Place Bet");
        placeBetExample.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: white; " +
            "-fx-text-fill: black; " +
            "-fx-padding: 8 25 8 25; " +
            "-fx-background-radius: 5; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        );
        placeBetExample.setOnAction(e -> {});
        HBox placeBetBox = new HBox(placeBetExample);
        placeBetBox.setPadding(new Insets(8, 0, 0, 0));
        VBox placeBetSection = new VBox(5, placeBetTitle, placeBetDesc, placeBetBox);
        
        // Hit Button
        Label hitBtnTitle = new Label("Hit Button:");
        hitBtnTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        Label hitBtnDesc = new Label("• Click to receive another card\n• Available during your turn\n• Disabled when it's not your turn or after you stand");
        hitBtnDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        hitBtnDesc.setWrapText(true);
        Button hitExample = new Button("Hit");
        hitExample.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: #2d5016; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 8 25 8 25; " +
            "-fx-background-radius: 5; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        );
        hitExample.setOnAction(e -> {});
        HBox hitBox = new HBox(hitExample);
        hitBox.setPadding(new Insets(8, 0, 0, 0));
        VBox hitBtnSection = new VBox(5, hitBtnTitle, hitBtnDesc, hitBox);
        
        // Stand Button
        Label standBtnTitle = new Label("Stand Button:");
        standBtnTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        Label standBtnDesc = new Label("• Click to end your turn and keep your current hand\n• Dealer will then reveal their cards and play\n• Cannot be undone once clicked");
        standBtnDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        standBtnDesc.setWrapText(true);
        Button standExample = new Button("Stand");
        standExample.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: #2d5016; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 8 25 8 25; " +
            "-fx-background-radius: 5; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        );
        standExample.setOnAction(e -> {});
        HBox standBox = new HBox(standExample);
        standBox.setPadding(new Insets(8, 0, 0, 0));
        VBox standBtnSection = new VBox(5, standBtnTitle, standBtnDesc, standBox);
        
        // Start Round Button
        Label startRoundTitle = new Label("Start Round Button:");
        startRoundTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        Label startRoundDesc = new Label("• Appears after a round ends\n• Click to begin a new round\n• Allows you to place a new bet");
        startRoundDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-family: 'Arial';");
        startRoundDesc.setWrapText(true);
        Button startRoundExample = new Button("Start Round");
        startRoundExample.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: white; " +
            "-fx-text-fill: black; " +
            "-fx-padding: 8 25 8 25; " +
            "-fx-background-radius: 5; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        );
        startRoundExample.setOnAction(e -> {});
        HBox startRoundBox = new HBox(startRoundExample);
        startRoundBox.setPadding(new Insets(8, 0, 0, 0));
        VBox startRoundSection = new VBox(5, startRoundTitle, startRoundDesc, startRoundBox);
        
        // Game Flow Summary
        Label flowTitle = new Label("Typical Game Flow:");
        flowTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
        Label flowDesc = new Label("1. Enter bet amount → 2. Place Bet → 3. Cards dealt → 4. Hit/Stand → 5. Round ends → 6. Start Round (repeat)");
        flowDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-family: 'Arial'; -fx-font-style: italic;");
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

    /**
     * Creates a card visual using Unicode symbols
     */
    private VBox createCardSymbol(String symbol, String value, String color) {
        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-font-size: 28px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + color + ";"
        );
        valueLabel.setAlignment(Pos.CENTER);
        
        Label symbolLabel = new Label(symbol);
        symbolLabel.setStyle(
            "-fx-font-size: 48px; " +
            "-fx-text-fill: " + color + ";"
        );
        symbolLabel.setAlignment(Pos.CENTER);
        
        VBox cardContent = new VBox(5, valueLabel, symbolLabel);
        cardContent.setAlignment(Pos.CENTER);
        cardContent.setStyle(
            "-fx-border-color: #333333; " +
            "-fx-border-width: 3px; " +
            "-fx-border-radius: 8; " +
            "-fx-background-color: white; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 15 25 15 25; " +
            "-fx-min-width: 90px; " +
            "-fx-min-height: 130px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        );
        
        return cardContent;
    }
}