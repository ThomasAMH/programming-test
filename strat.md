Genrally speaking, the goal is to recreate the app carefully and quickly. Let's begin!
Chessgame is untouched

ChessBoard...
    Obviously needs the board,
    The reset (and hardcoded positions)
    Add piece (-1 for pos)
    Get piece (-1 for pos)

    
Moving requires...
    The MoveResults
    Valid moves are on the board.
    Always OK on empty spaces (unless you're a pawn)
    Alwasy OK if enemy-occupied spaces.

Pawns require
    Just check the possible types of moves
    And promotion

A piece has...
    All the possible moves programmed into them.

All the code needs...
    Hash and to-string AFTER the variables are created!