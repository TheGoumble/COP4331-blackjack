package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
/**
 *
 * @author Bridjet Walker
 */
public class TutorialView extends BorderPane {

    public final Button exitButton = new Button("Exit Tutorial");

    public TutorialView() {
        setPadding(new Insets(20));

        Label title = new Label("How to Play Blackjack");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label body = new Label(
                """
                Basic Rules:
                • You start with a balance and place a bet each round.
                • You and the dealer each receive two cards. One of the dealer's cards is hidden.
                • Card values: 2–10 are face value, J/Q/K are 10, A is 1 or 11.
                • Your goal is to get as close to 21 as possible without going over (bust).

                Flow of a round:
                • Place your bet, then cards are dealt.
                • On your turn, you can Hit (take another card) or Stand (end your turn).
                • If you go over 21, you bust and lose your bet.
                • After you stand, the dealer draws until reaching at least 17.
                • If the dealer busts or your total is higher, you win. If lower, you lose. Same total is a push.

                Payouts:
                • Normal win: 1:1 (you get your bet back plus the same amount as profit).
                • Blackjack (21 with first two cards): paid 3:2.
                • Push: your bet is returned.

                In this app:
                • Enter a bet and click "Place Bet" to start a round.
                • Use Hit/Stand buttons during your turn.
                """
        );
        body.setWrapText(true);

        VBox content = new VBox(10, title, body);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(10));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);

        setCenter(scroll);
        setBottom(exitButton);
        BorderPane.setAlignment(exitButton, Pos.CENTER);
        BorderPane.setMargin(exitButton, new Insets(10, 0, 0, 0));
    }
}