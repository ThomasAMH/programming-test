package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    public enum MoveResult {
        LEGAL,
        ILLEGAL,
        CAPTURE,
        PROMOTE
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow();
        int column = position.getColumn();
        board[row-1][column-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        board[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        board[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        board[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);

        board[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        board[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);

        board[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        board[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);

        board[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        board[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);

        board[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        board[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);

        int i = 0;
        for(i = 0; i < 8; i++) {
            board[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            board[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
    }

    public MoveResult getMoveResult(ChessMove proposedMove) {
        System.out.println("Examining move " + proposedMove.toString());
        int startRow, startColumn, endRow, endColumn;
        startRow = proposedMove.getStartPosition().getRow();
        startColumn = proposedMove.getStartPosition().getColumn();
        endRow = proposedMove.getEndPosition().getRow();
        endColumn = proposedMove.getEndPosition().getColumn();

        //Check for off the board movements
        if(endRow < 1 || endColumn < 1 || endRow > 8 || endColumn > 8) {
            return MoveResult.ILLEGAL;
        }

        ChessPosition targetPiecePos = new ChessPosition(startRow, startColumn);
        ChessPiece targetPiece = getPiece(targetPiecePos);

        //Assume target piece will never be null
        if(targetPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return getPawnMoveResult(proposedMove);
        }

        ChessPosition endPiecePos = new ChessPosition(endRow, endColumn);
        ChessPiece pieceAtDestination = getPiece(endPiecePos);

        //Space is empty
        if(pieceAtDestination == null) {
            return MoveResult.LEGAL;
        }

        //Space is occupied
        if(targetPiece.getTeamColor() == pieceAtDestination.getTeamColor()) {
            return MoveResult.ILLEGAL;
        } else {
            return MoveResult.CAPTURE;
        }
    }
    private MoveResult getPawnMoveResult(ChessMove proposedMove) {
        int startRow, startColumn, endRow, endColumn;
        startRow = proposedMove.getStartPosition().getRow();
        startColumn = proposedMove.getStartPosition().getColumn();
        endRow = proposedMove.getEndPosition().getRow();
        endColumn = proposedMove.getEndPosition().getColumn();
        ChessPosition targetPiecePos = new ChessPosition(startRow, startColumn);
        ChessPiece targetPiece = getPiece(targetPiecePos);

        ChessGame.TeamColor movingPieceColor = targetPiece.getTeamColor();
        int directionMultiplier = 1;
        if(movingPieceColor == ChessGame.TeamColor.BLACK) {
            directionMultiplier = -1;
        }

        ChessPosition oneAheadPos = new ChessPosition(startRow + directionMultiplier, startColumn);
        ChessPiece pieceOneAhead = getPiece(oneAheadPos);

        //Check initial moves
        if(Math.abs(startRow - endRow) == 2) {
            if((startRow == 2 && movingPieceColor == ChessGame.TeamColor.WHITE) ||
               (startRow == 7 && movingPieceColor == ChessGame.TeamColor.BLACK)) {
                //Collision check
                ChessPosition twoAheadPos = new ChessPosition(startRow + (2*directionMultiplier), startColumn);
                ChessPiece pieceTwoAhead = getPiece(twoAheadPos);

                if(pieceOneAhead == null && pieceTwoAhead == null) {
                    return MoveResult.LEGAL;
                } else {
                    return MoveResult.ILLEGAL;
                }
            } else {
                return MoveResult.ILLEGAL;
            }
        }

        //Check Promotes
        boolean promoteFlag = false;
        if(movingPieceColor == ChessGame.TeamColor.WHITE && endRow == 8) {
            promoteFlag = true;
        } else if (movingPieceColor == ChessGame.TeamColor.BLACK && endRow == 1) {
            promoteFlag = true;
        }

        //Check captures
        if(startColumn != endColumn) {
            ChessPosition destinationPos = new ChessPosition(endRow, endColumn);
            ChessPiece pieceAtDestination = getPiece(destinationPos);
            if(pieceAtDestination == null || (pieceAtDestination.getTeamColor() == movingPieceColor)) {
                return MoveResult.ILLEGAL;
            } else {
                return promoteFlag ? MoveResult.PROMOTE : MoveResult.LEGAL;
            }
        }

        //Check one ahead movements
        if(Math.abs(startRow - endRow) == 1) {
            if(pieceOneAhead == null) return promoteFlag ? MoveResult.PROMOTE : MoveResult.LEGAL;
            else return MoveResult.ILLEGAL;
        }


        return MoveResult.LEGAL;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public String toString() {
        String returnString = "";
        ChessPiece currChessPiece;
        for(int i = 0; i <= 7; i++) {
            returnString += "\n";
            for(int j = 0; j <= 7; j++) {
                currChessPiece = board[i][j];
                if(currChessPiece != null) {
                    returnString += board[i][j].toString() + "|";
                } else {
                    returnString += " ";
                }

            }
        }
        return returnString;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
