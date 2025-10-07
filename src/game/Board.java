package game;

import java.util.ArrayList;
import java.util.List;
import java.awt.Point;

public class Board {
    public static final int SIZE = 10;
    private final Piece[][] grid;

    public Board() {
        grid = new Piece[SIZE][SIZE];
        setupPieces();
    }

    private void setupPieces() {
        // Player 1 (15 peças)
        grid[0][0] = new Piece(1); grid[0][1] = new Piece(1); grid[0][2] = new Piece(1); grid[0][3] = new Piece(1); grid[0][4] = new Piece(1);
        grid[1][0] = new Piece(1); grid[1][1] = new Piece(1); grid[1][2] = new Piece(1); grid[1][3] = new Piece(1);
        grid[2][0] = new Piece(1); grid[2][1] = new Piece(1); grid[2][2] = new Piece(1);
        grid[3][0] = new Piece(1); grid[3][1] = new Piece(1);
        grid[4][0] = new Piece(1);

        // Player 2 (15 peças)
        grid[SIZE - 1][SIZE - 1] = new Piece(2); grid[SIZE - 1][SIZE - 2] = new Piece(2); grid[SIZE - 1][SIZE - 3] = new Piece(2); grid[SIZE - 1][SIZE - 4] = new Piece(2); grid[SIZE - 1][SIZE - 5] = new Piece(2);
        grid[SIZE - 2][SIZE - 1] = new Piece(2); grid[SIZE - 2][SIZE - 2] = new Piece(2); grid[SIZE - 2][SIZE - 3] = new Piece(2); grid[SIZE - 2][SIZE - 4] = new Piece(2);
        grid[SIZE - 3][SIZE - 1] = new Piece(2); grid[SIZE - 3][SIZE - 2] = new Piece(2); grid[SIZE - 3][SIZE - 3] = new Piece(2);
        grid[SIZE - 4][SIZE - 1] = new Piece(2); grid[SIZE - 4][SIZE - 2] = new Piece(2);
        grid[SIZE - 5][SIZE - 1] = new Piece(2);
    }

    /**
     * Calcula e retorna uma lista de movimentos IMEDIATOS válidos para uma peça.
     * Não mostra mais os saltos em cadeia.
     */
    public List<Point> getValidMoves(int startRow, int startCol, boolean inChainJump) {
        List<Point> validMoves = new ArrayList<>();
        Piece piece = getPieceAt(startRow, startCol);
        if (piece == null) {
            return validMoves;
        }

        // 1. Se não estiver numa cadeia de saltos, adiciona movimentos de 1 casa.
        if (!inChainJump) {
            for (int r = -1; r <= 1; r++) {
                for (int c = -1; c <= 1; c++) {
                    if (r == 0 && c == 0) continue;
                    int destRow = startRow + r;
                    int destCol = startCol + c;
                    if (isSingleStepValid(destRow, destCol)) {
                        validMoves.add(new Point(destRow, destCol));
                    }
                }
            }
        }
        
        // 2. Adiciona APENAS o primeiro nível de saltos possíveis.
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;

                int jumpedPieceRow = startRow + dr;
                int jumpedPieceCol = startCol + dc;
                int landingRow = startRow + dr * 2;
                int landingCol = startCol + dc * 2;

                if (isJumpValid(jumpedPieceRow, jumpedPieceCol, landingRow, landingCol)) {
                    validMoves.add(new Point(landingRow, landingCol));
                }
            }
        }

        return validMoves;
    }
    
    /**
     * Verifica se um movimento de uma única casa é válido.
     */
    private boolean isSingleStepValid(int endRow, int endCol) {
        return isValidCoordinate(endRow, endCol) && grid[endRow][endCol] == null;
    }
    
    /**
     * Verifica se um salto é válido (casa intermediária ocupada, casa final vazia).
     */
    private boolean isJumpValid(int jumpedRow, int jumpedCol, int landingRow, int landingCol) {
        return isValidCoordinate(landingRow, landingCol) && 
               grid[landingRow][landingCol] == null &&
               isValidCoordinate(jumpedRow, jumpedCol) && 
               grid[jumpedRow][jumpedCol] != null;
    }

    public boolean movePiece(int startRow, int startCol, int endRow, int endCol, int player, boolean inChainJump) {
        Piece piece = getPieceAt(startRow, startCol);
        if (piece == null || piece.getPlayerId() != player) {
            return false;
        }
        
        List<Point> validMoves = getValidMoves(startRow, startCol, inChainJump);
        
        for (Point move : validMoves) {
            if (move.x == endRow && move.y == endCol) {
                performMove(startRow, startCol, endRow, endCol);
                return true;
            }
        }

        return false;
    }

    public boolean canJumpFrom(int row, int col) {
        if (getPieceAt(row, col) == null) return false;
        
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                if (isJumpValid(row + dr, col + dc, row + dr * 2, col + dc * 2)) {
                    return true;
                }
            }
        }
        return false;
    }

    // --- MÉTODOS AUXILIARES (sem alterações) ---
    
    public Piece getPieceAt(int row, int col) {
        if (isValidCoordinate(row, col)) return grid[row][col];
        return null;
    }

    public void performMove(int startRow, int startCol, int endRow, int endCol) {
        if (getPieceAt(startRow, startCol) == null) return;
        Piece piece = grid[startRow][startCol];
        grid[startRow][startCol] = null;
        grid[endRow][endCol] = piece;
    }

    private boolean isValidCoordinate(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }
    
    public boolean checkForWinner(int player) {
        if (player == 1) {
            if (getPieceAt(SIZE-1, SIZE-1) == null || getPieceAt(SIZE-1, SIZE-1).getPlayerId() != 1) return false; if (getPieceAt(SIZE-1, SIZE-2) == null || getPieceAt(SIZE-1, SIZE-2).getPlayerId() != 1) return false; if (getPieceAt(SIZE-1, SIZE-3) == null || getPieceAt(SIZE-1, SIZE-3).getPlayerId() != 1) return false; if (getPieceAt(SIZE-1, SIZE-4) == null || getPieceAt(SIZE-1, SIZE-4).getPlayerId() != 1) return false; if (getPieceAt(SIZE-1, SIZE-5) == null || getPieceAt(SIZE-1, SIZE-5).getPlayerId() != 1) return false; if (getPieceAt(SIZE-2, SIZE-1) == null || getPieceAt(SIZE-2, SIZE-1).getPlayerId() != 1) return false; if (getPieceAt(SIZE-2, SIZE-2) == null || getPieceAt(SIZE-2, SIZE-2).getPlayerId() != 1) return false; if (getPieceAt(SIZE-2, SIZE-3) == null || getPieceAt(SIZE-2, SIZE-3).getPlayerId() != 1) return false; if (getPieceAt(SIZE-2, SIZE-4) == null || getPieceAt(SIZE-2, SIZE-4).getPlayerId() != 1) return false; if (getPieceAt(SIZE-3, SIZE-1) == null || getPieceAt(SIZE-3, SIZE-1).getPlayerId() != 1) return false; if (getPieceAt(SIZE-3, SIZE-2) == null || getPieceAt(SIZE-3, SIZE-2).getPlayerId() != 1) return false; if (getPieceAt(SIZE-3, SIZE-3) == null || getPieceAt(SIZE-3, SIZE-3).getPlayerId() != 1) return false; if (getPieceAt(SIZE-4, SIZE-1) == null || getPieceAt(SIZE-4, SIZE-1).getPlayerId() != 1) return false; if (getPieceAt(SIZE-4, SIZE-2) == null || getPieceAt(SIZE-4, SIZE-2).getPlayerId() != 1) return false; if (getPieceAt(SIZE-5, SIZE-1) == null || getPieceAt(SIZE-5, SIZE-1).getPlayerId() != 1) return false;
            return true;
        } else {
            if (getPieceAt(0, 0) == null || getPieceAt(0, 0).getPlayerId() != 2) return false; if (getPieceAt(0, 1) == null || getPieceAt(0, 1).getPlayerId() != 2) return false; if (getPieceAt(0, 2) == null || getPieceAt(0, 2).getPlayerId() != 2) return false; if (getPieceAt(0, 3) == null || getPieceAt(0, 3).getPlayerId() != 2) return false; if (getPieceAt(0, 4) == null || getPieceAt(0, 4).getPlayerId() != 2) return false; if (getPieceAt(1, 0) == null || getPieceAt(1, 0).getPlayerId() != 2) return false; if (getPieceAt(1, 1) == null || getPieceAt(1, 1).getPlayerId() != 2) return false; if (getPieceAt(1, 2) == null || getPieceAt(1, 2).getPlayerId() != 2) return false; if (getPieceAt(1, 3) == null || getPieceAt(1, 3).getPlayerId() != 2) return false; if (getPieceAt(2, 0) == null || getPieceAt(2, 0).getPlayerId() != 2) return false; if (getPieceAt(2, 1) == null || getPieceAt(2, 1).getPlayerId() != 2) return false; if (getPieceAt(2, 2) == null || getPieceAt(2, 2).getPlayerId() != 2) return false; if (getPieceAt(3, 0) == null || getPieceAt(3, 0).getPlayerId() != 2) return false; if (getPieceAt(3, 1) == null || getPieceAt(3, 1).getPlayerId() != 2) return false; if (getPieceAt(4, 0) == null || getPieceAt(4, 0).getPlayerId() != 2) return false;
            return true;
        }
    }
}