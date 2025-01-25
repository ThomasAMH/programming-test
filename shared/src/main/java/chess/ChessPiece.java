package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor teamColor;
    private PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        teamColor = pieceColor;
        pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    private final String[] rookMoves = {"0,1", "0,-1", "1,0", "-1,0"};
    private final String[] bishopMoves = {"1,1", "1,-1", "-1,1", "-1,-1"};
    private final String[] queenMoves = {"1,1", "1,-1", "-1,1", "-1,-1", "0,1", "0,-1", "1,0", "-1,0"};
    private final String[] knightMoves = {"1,2", "-1,2", "1,-2", "-1,-2", "2,1", "2,-1", "-2,1", "-2,-1"};
    private final String[] kingMoves = {"1,1", "1,-1", "-1,1", "-1,-1", "0,1", "0,-1", "1,0", "-1,0"};
    private final String[] pawnMoves = {"2,0", "1,0", "1,1", "1,-1"};

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        return switch (pieceType) {
            case KING -> checkFixedMoves(board, myPosition, kingMoves);
            case KNIGHT -> checkFixedMoves(board, myPosition, knightMoves);
            case QUEEN -> checkIterativeMoves(board, myPosition, queenMoves);
            case BISHOP -> checkIterativeMoves(board, myPosition, bishopMoves);
            case ROOK -> checkIterativeMoves(board, myPosition, rookMoves);
            case PAWN -> checkPawnMoves(board, myPosition, pawnMoves);
            default -> throw new IllegalStateException("Unexpected value: " + pieceType);
        };
    }

    private Collection<ChessMove> checkIterativeMoves(ChessBoard board, ChessPosition myPosition, String[] moveSet) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int rowDelta = 0, columnDelta = 0;
        int initRowDelta, initColumnDelta;
        ChessBoard.MoveResult moveResult;
        for(String move : moveSet) {
            initRowDelta = Integer.parseInt(move.split(",")[0]);
            initColumnDelta = Integer.parseInt(move.split(",")[1]);

            while(true) {
                rowDelta += initRowDelta ;
                columnDelta += initColumnDelta;
                ChessPosition endPosition = new ChessPosition(myPosition.getRow() + rowDelta,
                        myPosition.getColumn() + columnDelta);
                ChessMove proposedMove = new ChessMove(myPosition, endPosition, null);
                moveResult = board.getMoveResult(proposedMove);
                if(moveResult == ChessBoard.MoveResult.LEGAL) {
                    possibleMoves.add(proposedMove);
                } else if (moveResult == ChessBoard.MoveResult.CAPTURE) {
                    possibleMoves.add(proposedMove);
                    break;
                } else {
                    break;
                }
            }
            rowDelta = 0;
            columnDelta = 0;
        }
        return possibleMoves;
    }


    private Collection<ChessMove> checkFixedMoves(ChessBoard board, ChessPosition myPosition, String[] moveSet) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int rowDelta = 0, columnDelta = 0;
        int initRowDelta, initColumnDelta;
        boolean illegalMoveFlag = false;
        ChessBoard.MoveResult moveResult;
        for(String move : moveSet) {
            rowDelta = Integer.parseInt(move.split(",")[0]);
            columnDelta = Integer.parseInt(move.split(",")[1]);

            ChessPosition endPosition = new ChessPosition(myPosition.getRow() + rowDelta,
                    myPosition.getColumn() + columnDelta);

            ChessMove proposedMove = new ChessMove(myPosition, endPosition, null);
            moveResult = board.getMoveResult(proposedMove);
            if(moveResult != ChessBoard.MoveResult.ILLEGAL) {
                possibleMoves.add(proposedMove);
            }
        }
        return possibleMoves;
    }

    private Collection<ChessMove> checkPawnMoves(ChessBoard board, ChessPosition myPosition, String[] moveSet) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int rowDelta = 0, columnDelta = 0;
        ChessMove proposedMove;
        int directionMultiplier = 1;
        if(teamColor == ChessGame.TeamColor.BLACK) {
            directionMultiplier = -1;
        }
        for(String move: moveSet) {
            rowDelta = Integer.parseInt(move.split(",")[0]);
            columnDelta = Integer.parseInt(move.split(",")[1]);
            ChessPosition endPosition = new ChessPosition(myPosition.getRow() + (rowDelta*directionMultiplier),
                    myPosition.getColumn() + (columnDelta*directionMultiplier));
            proposedMove = new ChessMove(myPosition, endPosition, null);

            ChessBoard.MoveResult moveResult = board.getMoveResult(proposedMove);
            if(moveResult == ChessBoard.MoveResult.LEGAL || moveResult == ChessBoard.MoveResult.CAPTURE) {
                possibleMoves.add(proposedMove);
            } else if (moveResult == ChessBoard.MoveResult.PROMOTE) {
                proposedMove = new ChessMove(myPosition, endPosition, PieceType.ROOK);
                possibleMoves.add(proposedMove);
                proposedMove = new ChessMove(myPosition, endPosition, PieceType.KNIGHT);
                possibleMoves.add(proposedMove);
                proposedMove = new ChessMove(myPosition, endPosition, PieceType.BISHOP);
                possibleMoves.add(proposedMove);
                proposedMove = new ChessMove(myPosition, endPosition, PieceType.QUEEN);
                possibleMoves.add(proposedMove);
            }
        }

        return possibleMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && pieceType == that.pieceType && Objects.deepEquals(rookMoves, that.rookMoves) && Objects.deepEquals(bishopMoves, that.bishopMoves) && Objects.deepEquals(queenMoves, that.queenMoves) && Objects.deepEquals(knightMoves, that.knightMoves) && Objects.deepEquals(kingMoves, that.kingMoves) && Objects.deepEquals(pawnMoves, that.pawnMoves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType, Arrays.hashCode(rookMoves), Arrays.hashCode(bishopMoves), Arrays.hashCode(queenMoves), Arrays.hashCode(knightMoves), Arrays.hashCode(kingMoves), Arrays.hashCode(pawnMoves));
    }

    @Override
    public String toString() {
        String pieceChar ;
        switch (pieceType) {
            case KING -> pieceChar = "k";
            case KNIGHT -> pieceChar = "h";
            case QUEEN -> pieceChar = "q";
            case BISHOP -> pieceChar = "b";
            case ROOK -> pieceChar = "r";
            case PAWN -> pieceChar = "p";
            default -> pieceChar = "?";
        }
        if(teamColor == ChessGame.TeamColor.BLACK) {
            return pieceChar;
        } else {
            return pieceChar.toUpperCase();
        }
    }


}