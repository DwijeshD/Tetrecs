package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.game.GamePiece;
/**
 * The Next Piece listener is used to handle the event when the user clicks on a block, and a
 * new piece is generated. It passes the current GamePiece
 */
public interface NextPieceListener {

    /**
     * Handle next piece event
     * @param nextPiece the next piece to be played
     * @param followingPiece the following piece afterwards
     */
    public void nextPiece(GamePiece nextPiece, GamePiece followingPiece);

}
