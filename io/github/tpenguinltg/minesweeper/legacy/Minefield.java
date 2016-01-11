package io.github.tpenguinltg.minesweeper.legacy;

import java.awt.Point;
import java.util.LinkedList;

/**
 * An abstract minesweeper grid.
 * @author tPenguinLTG
 * @version 1.0<br>
 *          Created 12 Jan 2012<br>
 *          Modified 26 Jan 2012
 */
public class Minefield
  {
  public static final int DISABLED = -1;
  public static final int PLAYING = 0;
  public static final int PENDING = 1;
  public static final int LOST = 2;
  public static final int WON = 3;
  
  private int width, height, mineCount, flagsLeft;
  private Point[] mineLocations;
  private LinkedList<Point> flagLocations;
  private Cell[][] grid;
  private int gameState;
  private boolean useQuestions;
  
  /**
   * Creates a new Minefield from an existing grid.
   * @param oldGrid the grid to create the Minefield from
   */
  public Minefield(Cell[][] oldGrid)
    {
    this.grid = oldGrid;
    this.width = oldGrid.length;
    this.height = oldGrid[0].length;
    
    this.mineCount = getMineCount(oldGrid);
    
    this.flagsLeft = getFlagsLeftCount(oldGrid);
    
    this.flagLocations = getFlagLocations(oldGrid);
    
    this.mineLocations = getMineLocations(oldGrid);
    
    setGameState(PENDING);
    }// end constructor Minesweeper(GridSpace[][])
    
  /**
   * Creates a new Minefield with the given parameters.
   * @param width the width of the grid
   * @param height the height of the grid
   * @param mineCount the number of mines
   */
  public Minefield(int width, int height, int mineCount)
                                                        throws IllegalArgumentException
    {
    
    // IF any of the arguments cannot logically be used
    if (width <= 0)
      {
      throw new IllegalArgumentException("The width must be greater than 0.");
      }
    else if (height <= 0)
      {
      throw new IllegalArgumentException("The height must be greater than 0.");
      }
    else if (mineCount < 0)
      {
      throw new IllegalArgumentException(
        "The number of mines must be positive.");
      }
    else if(mineCount > width * height)
      {
      throw new IllegalArgumentException("The number of mines cannot be greater than the number of cells.");
      }// end if
      
    this.width = width;
    this.height = height;
    this.mineCount = mineCount;
    this.flagsLeft = mineCount;
    this.grid = new Cell[width][height];
    this.mineLocations = new Point[mineCount];
    this.flagLocations = new LinkedList<Point>();
    this.useQuestions = true;
    
    initializeGrid();
    
    setGameState(PENDING);
    }// end constructor Minesweeper(int, int, int)
    
  public static int getFlagCount(Cell[][] gridToUse)
    {
    int _flagCount = 0;
    
    // DO for each cell
    for (Cell[] column : gridToUse)
      {
      
      for (Cell cell : column)
        {
        
        // IF this cell is a mine
        if (cell.isFlagged())
          {
          _flagCount++;
          }// end if
          
        }// end for cell:column
        
      }// end for cell:gridToUse
      
    return _flagCount;
    }// end getFlagCount()
    
  public static LinkedList<Point> getFlagLocations(Cell[][] gridToUse)
    {
    LinkedList<Point> _flagLocations = new LinkedList<Point>();
    int flagId = 0;
    
    // DO for each cell in the grid
    column:
    for (int x = 0; x < gridToUse.length; x++)
      {
      
      for (int y = 0; y < gridToUse[x].length; y++)
        {
        // IF this cell contains a mine
        if (gridToUse[x][y].isMine())
          {
          // add this cell to the list of mines
          _flagLocations.add(new Point(x, y));
          
          // next flagId
          flagId++;
          
          // IF there are no more mines to record
          if (flagId >= _flagLocations.size())
            {
            break column;
            }// end if
            
          }// end if
        }// end for y
        
      }// end for x
      
    return _flagLocations;
    }// end getFlagLocations
    
  /**
   * Gets the number of flags in the given grid
   * @param gridToUse the grid to count flags in
   * @return the number of flags in the grid
   */
  public static int getFlagsLeftCount(Cell[][] gridToUse)
    {
    int _flagCount = 0;
    
    // DO for each cell
    for (Cell[] column : gridToUse)
      {
      
      for (Cell cell : column)
        {
        
        // IF this cell is flagged
        if (cell.isFlagged())
          {
          _flagCount++;
          }// end if
          
        }// end for column:gridToUse
        
      }// end for cell:column
      
    return getMineCount(gridToUse) - _flagCount;
    }// end getFlagCount()
    
  /**
   * Gets the number of mines in the given grid
   * @param gridToUse the grid to count mines in
   * @return the number of mines in the grid
   */
  public static int getMineCount(Cell[][] gridToUse)
    {
    int _mineCount = 0;
    
    // DO for each cell
    for (Cell[] column : gridToUse)
      {
      
      for (Cell cell : column)
        {
        
        // IF this cell is a mine
        if (cell.isMine())
          {
          _mineCount++;
          }// end if
          
        }// end for cell:column
        
      }// end for cell:gridToUse
      
    return _mineCount;
    }// end getMineCount()
    
  /**
   * Returns the locations of the mines in the given grid
   * @param gridToUse the grid to extract the mine locations
   * @return an array of Points representing the locations of the mines
   */
  public static Point[] getMineLocations(Cell[][] gridToUse)
    {
    Point[] _mineLocations = new Point[getMineCount(gridToUse)];
    int mineId = 0;
    
    // DO for each cell in the grid
    column:
    for (int x = 0; x < gridToUse.length; x++)
      {
      
      for (int y = 0; y < gridToUse[x].length; y++)
        {
        // IF this cell contains a mine
        if (gridToUse[x][y].isMine())
          {
          // add this cell to the list of mines
          _mineLocations[mineId] = new Point(x, y);
          
          // next mineId
          mineId++;
          
          // IF there are no more mines to record
          if (mineId >= _mineLocations.length)
            {
            break column;
            }// end if
            
          }// end if
        }// end for y
        
      }// end for x
      
    return _mineLocations;
    }// end getMineLocations()
    
    
  /**
   * Random integer generator.
   * @param lowerLimit The lower limit.
   * @param upperLimit The upper limit.
   * @return A random integer from lowerLimit to upperLimit inclusive
   */
  public static int randInt(int lowerLimit, int upperLimit)
    {
    return (int) Math.floor(Math.random() * (upperLimit - lowerLimit + 1) +
      lowerLimit);
    }// end randInt()
    
  public boolean addMine(int x, int y, int mineId)
                                                  throws IllegalArgumentException
    {
    return addMine(x, y, mineId, true);
    }// end addMine(int,int,int)
    
  /**
   * Adds the mine at the specified location
   * @param x the x-coordinate of the mine
   * @param y the y-coordinate of the mine
   * @param mineId the ID of the mine
   * @param register add 1 to the surrounding mine count of the surrounding cells
   * @return true if successful; false otherwise
   * @throws IllegalArgumentException if the coordinates are out of bounds
   */
  public boolean addMine(int x, int y, int mineId, boolean register)
                                                                    throws IllegalArgumentException
    {
    
    try
      {
      // IF there is already a mine in this space
      if (grid[x][y].isMine())
        {
        return false;
        }// end if
      }
    catch (ArrayIndexOutOfBoundsException e)
      {
      /* grid[x][y] will throw an ArrayIndexOutOfBoundsException if x or y is
       * out of bounds. */
      
      // convert this exception to IllegalArgumentException
      throw new IllegalArgumentException("Arguments out of range.");
      }// end try..catch
      
    grid[x][y].addMine(mineId);
    
    // IF the registration of the mine is activated
    if (register)
      {
      registerMine(x, y);
      }// end if
      
    return true;
    }// end addMine(int,int,int,boolean)
    
  public boolean addMine(Point location, int mineId)
                                                    throws IllegalArgumentException
    {
    return addMine(location.x, location.y, mineId);
    }// end addMine(Point,int)
    
  public boolean addMine(Point location, int mineId, boolean register)
                                                                      throws IllegalArgumentException
    {
    return addMine(location.x, location.y, mineId, register);
    }// end addMine(Point, int, boolean)
    
  /**
   * Moves the mine at the given location to the first available cell, starting at (0,0).
   * This method does nothing if there is no mine at the specified location.
   * @param x the x-coordinate of the mine to move
   * @param y the y-coordinate of the mine to move
   */
  public void firstTurnMoveMine(int x, int y)
    {
    // IF there is no mine at this cell
    if (!grid[x][y].isMine())
      {
      return;
      }// end if
      
    // move it to the first empty cell, starting at 0,0
    for (int newX = 0; newX < grid.length; newX++)
      {
      
      for (int newY = 0; newY < grid[newX].length; newY++)
        {
        
        // IF moving of the mine is successful
        if (moveMine(x, y, newX, newY, grid[x][y].getMineId()))
          {
          return;
          }// end if
          
        // ELSE continue to the next cell
        }// end for newY
        
      }// end for newX
      
    }// end firstTurnMoveMine(int,int)
    
  public void firstTurnMoveMine(Point cellActivated)
    {
    firstTurnMoveMine(cellActivated.x, cellActivated.y);
    }// end firstTurnMoveMine(Point)
    
  public void flagAllMines()
    {
    // DO for each mine
    for (Point mine : mineLocations)
      {
      setFlag(true, mine);
      }// end for
    }// end flagAllMines
    
  public void forgetAllMines()
    {
    forgetAllMines(false);
    }// end forgetAllMines()
    
  public void forgetAllMines(boolean forceRegistration)
    {
    // DO for each mine
    for (Point mine : mineLocations)
      {
      forgetMine(mine);
      }// end for mine:mineLocations
    }// end forgetAllMines(boolean)
    
  public void forgetMine(int x, int y) throws IllegalArgumentException
    {
    registerMine(x, y, false);
    }// end forgetMine(int,int)
    
  /**
   * Subtracts 1 from the surrounding mine count of the cells around
   * @param x the x-coordinate of the mine
   * @param y the y-coordinate of the mine
   * @param forceRegistration true to force the subtraction even if there is no mine; false otherwise
   */
  public void forgetMine(int x, int y, boolean forceRegistration)
                                                                 throws IllegalArgumentException
    {
    // IF arguments are out of bounds
    if (x >= width || y >= height)
      {
      /* grid[x][y] will throw an ArrayIndexOutOfBoundsException if x or y is
       * out of bounds. */
      
      throw new IllegalArgumentException("Arguments out of range.");
      }// end if
      
    if (!grid[x][y].isMine() || (grid[x][y].isMine() && forceRegistration))
      {
      
      // DO for each cell around this cell
      for (int dx = -1; dx <= 1; dx++)
        {
        
        for (int dy = -1; dy <= 1; dy++)
          {
          
          // IF it is this cell
          if (dx == 0 && dy == 0)
            {
            continue;
            }// end if
            
          // IF this cell is out of bounds
          if ((x + dx < 0 || x + dx >= width) ||
            (y + dy < 0 || y + dy >= height))
            {
            continue;
            }// end if
            
          grid[x + dx][y + dy].forgetSurroundingMine();
          
          }// end for dy
          
        }// end for dx
        
      }// end if
      
    }// end forgetMine(int,int,boolean)
    
  public void forgetMine(Point location)
    {
    forgetMine(location.x, location.y);
    }// end forgetMine(Point)
    
  public void forgetMine(Point location, boolean forceRegistration)
    {
    forgetMine(location.x, location.y, forceRegistration);
    }// end forgetMine(Point, boolean)
    
  public int getCellState(int x, int y)
    {
    return grid[x][y].getState();
    }// end getCellState()
    
  public int getFlagsLeftCount()
    {
    return flagsLeft;
    }// end getFlagsLeftCount()
    
  public int getGameState()
    {
    return gameState;
    }// end gameState()
    
  public Cell[][] getGrid()
    {
    return grid;
    }// end getGrid()
    
  public int getHeight()
    {
    return height;
    }// end getHeight()
    
  public Point[] getMineLocations()
    {
    return mineLocations;
    }// end getMineLocations()
    
  public int getWidth()
    {
    return width;
    }// end getWidth()
    
  public boolean isCellRevealed(int x, int y)
    {
    return grid[x][y].isRevealed();
    }// end isCellRevealed()
    
  public boolean isMine(int x, int y)
    {
    return grid[x][y].isMine();
    }// end isMine()
    
  /**
   * Reveal all mines without detonating them.
   */
  public void loseReveal()
    {
    // DO for each mine
    for (Point mine : mineLocations)
      {
      
      // IF this mine is not flagged
      if (!grid[mine.x][mine.y].isFlagged())
        {
        // reveal this cell
        grid[mine.x][mine.y].setRevealState(true);
        }// end if
        
      }// end for mine:mineLocations
      
      
    // DO for each flag
    for (int flagId = 0; flagId < flagLocations.size(); flagId++)
      {
      
      Point flag = flagLocations.get(flagId);
      
      // IF this flag does not contain a mine
      if (grid[flag.x][flag.y].isSafe())
        {
        // reveal this cell
        grid[flag.x][flag.y].setRevealState(true);
        }// end if
        
      }// end for flag:flagLocations
      
    }// end loseReveal()
    
  public boolean moveMine(int fromX, int fromY, int toX, int toY, int mineId)
    {
    return moveMine(fromX, fromY, toX, toY, mineId, true);
    }
  
  public boolean moveMine(int fromX, int fromY, int toX, int toY, int mineId,
                          boolean register)
    {
    try
      {
      // IF the source cell does not contain a mine or if the destination cell
      // contains a mine
      if (!grid[fromX][fromY].isMine() || grid[toX][toY].isMine())
        {
        return false;
        }// end if
      }
    catch (ArrayIndexOutOfBoundsException e)
      {
      /* grid[x][y] will throw an ArrayIndexOutOfBoundsException if x or y is
       * out of bounds. */
      
      // convert this exception to IllegalArgumentException
      throw new IllegalArgumentException("Arguments out of range.");
      }// end try..catch
      
    // add mine to destination
    grid[toX][toY].addMine(mineId);
    
    // remove mine from source
    grid[fromX][fromY].removeMine();
    
    // update mine locations
    mineLocations[mineId] = new Point(toX, toY);
    
    // IF registering of this mine is enabled
    if (register)
      {
      registerMine(toX, toY);
      forgetMine(fromX, fromY);
      }// end if
      
    return true;
    }// end moveMine()
    
  public boolean moveMine(Point source, Point destination, int mineId)
    {
    return moveMine(source.x, source.y, destination.x, destination.y, mineId);
    }// end moveMine()
    
  public void placeMinesRandomly()
    {
    // DO for each mine to place
    for (int mineId = 0; mineId < mineCount; mineId++)
      {
      // try to place mine
      boolean successful;
      int x, y;
      do
        {
        // attempt to place a mine in a random location
        
        x = randInt(0, width - 1);
        y = randInt(0, height - 1);
        successful = addMine(x, y, mineId, false);
        
        // if there is already a mine in that cell, try again
        } while (!successful);
      
      // add this mine to the list of mines
      mineLocations[mineId] = new Point(x, y);
      }// end for
    }// end placeMinesRandomly()
    
  public void registerAllMines()
    {
    registerAllMines(false);
    }// end registerAllMines()
    
  public void registerAllMines(boolean forceRegistration)
    {
    // DO for each mine
    for (Point mine : mineLocations)
      {
      registerMine(mine);
      }// end for mine:mineLocations
    }// end registerAllMines(boolean)
    
  public void registerMine(int x, int y) throws IllegalArgumentException
    {
    registerMine(x, y, false);
    }// end registerMine(int,int)
    
  /**
   * Adds 1 to the surrounding mine count of the surrounding cells
   * @param x the x-coordinate of the mine
   * @param y the y-coordinate of the mine
   * @param forceRegistration true to register even if there is no mine; false otherwise
   */
  public void registerMine(int x, int y, boolean forceRegistration)
                                                                   throws IllegalArgumentException
    {
    // IF arguments are out of bounds
    if (x >= width || y >= height)
      {
      /* grid[x][y] will throw an ArrayIndexOutOfBoundsException if x or y is
       * out of bounds. */
      
      throw new IllegalArgumentException("Arguments out of range.");
      }// end if
      
    if (grid[x][y].isMine() || (!grid[x][y].isMine() && forceRegistration))
      {
      
      // DO for each cell around this cell
      for (int dx = -1; dx <= 1; dx++)
        {
        
        for (int dy = -1; dy <= 1; dy++)
          {
          
          // IF it is this cell
          if (dx == 0 && dy == 0)
            {
            continue;
            }// end if
            
          // IF this cell is out of bounds
          if ((x + dx < 0 || x + dx >= width) ||
            (y + dy < 0 || y + dy >= height))
            {
            continue;
            }// end if
            
          grid[x + dx][y + dy].registerSurroundingMine();
          
          }// end for dy
          
        }// end for dx
        
      }// end if
      
    }// end registerMine(int,int,boolean)
    
  public void registerMine(Point location) throws IllegalArgumentException
    {
    registerMine(location.x, location.y);
    }// end registerMine(Point)
    
  public void registerMine(Point location, boolean forceRegistration)
                                                                     throws IllegalArgumentException
    {
    registerMine(location.x, location.y, forceRegistration);
    }// end registerMine(Point, boolean)
    
  public void revealCell(int x, int y)
    {
    // IF this is the first move and
    // this cell is a mine
    if (getGameState() == PENDING && grid[x][y].isMine())
      {
      // move this mine to the top-left corner (or the next available cell)
      firstTurnMoveMine(x, y);
      }// end if
      
    setGameState(PLAYING);
    
    // IF this cell is already revealed
    if (grid[x][y].isRevealed())
      {
      // do nothing
      return;
      }// end if
      
    // reveal this cell
    setRevealState(x, y, true);
    
    
    // IF this cell is zero
    if (grid[x][y].getState() == Cell.ZERO)
      {
      revealSurroundingCells(x, y, false);
      }
    // ELSEIF this cell is a mine
    else if (grid[x][y].isMine())
      {
      grid[x][y].detonate();
      
      setGameState(LOST);
      }// end if
    }// end revealCell(int,int)
    
  public void revealCell(Point cell)
    {
    revealCell(cell.x, cell.y);
    }// end revealCell(Point)
    
  public boolean revealSurroundingCells(int x, int y)
    {
    return revealSurroundingCells(x, y, false);
    }
  
  public boolean revealSurroundingCells(int x, int y, boolean forceReveal)
    {
    Cell cell = grid[x][y];
    
    // IF the number of surrounding flags does not match the number of
    // surrounding mines
    if (!cell.isRevealed() ||
      cell.getSurroundingFlagCount() != cell.getSurroundingMineCount())
      {
      return false;
      }// end if
      
      
    // DO for each surrounding cell
    for (int dx = -1; dx <= 1; dx++)
      {
      for (int dy = -1; dy <= 1; dy++)
        {
        
        // IF out of bounds
        if (x + dx < 0 || x + dx >= width || y + dy < 0 || y + dy >= height)
          {
          continue;
          }// end if
          
          
        // IF it is this cell or if it is already revealed or if it is flagged
        // and not revealing is not
        // forced
        if (dx == 0 && dy == 0 || grid[x + dx][y + dy].isRevealed() ||
          !forceReveal && grid[x + dx][y + dy].isFlagged())
          {
          continue;
          }// end if
          
          
        revealCell(x + dx, y + dy);
        
        }// end for dy
      }// end for dx
      
    return true;
    }// end revealSurroundingCells()
    
  public boolean revealSurroundingCells(Point cell)
    {
    return revealSurroundingCells(cell.x, cell.y, false);
    }// end revealSurroundingCells(Point)
    
  public boolean revealSurroundingCells(Point cell, boolean forceReveal)
    {
    return revealSurroundingCells(cell.x, cell.y, forceReveal);
    }// end revealSurroundingCells(Point, boolean)
    
  public void setFlag(boolean flagState, int x, int y)
                                                      throws IllegalArgumentException
    {
    // IF this cell is already set to that state
    if (grid[x][y].isFlagged() == flagState)
      {
      // do nothing
      return;
      }
    
    try
      {
      
      grid[x][y].setFlagState(flagState);
      
      // IF the flag is to be added
      if (flagState == true)
        {
        // add the flag to the list
        flagLocations.add(new Point(x, y));
        
        flagsLeft--;
        
        // DO for each surrounding cell
        for (int dx = -1; dx <= 1; dx++)
          {
          for (int dy = -1; dy <= 1; dy++)
            {
            // IF out of bounds
            if (x + dx < 0 || x + dx >= width || y + dy < 0 || y + dy >= height)
              {
              // next cell
              continue;
              }// end if
              
            // register this flag with this cell
            grid[x + dx][y + dy].registerSurroundingFlag();
            }// end for dy
          }// end for dx
        }
      else
        {
        // remove the flag from the list
        flagLocations.remove(new Point(x, y));
        
        flagsLeft++;
        
        // DO for each surrounding cell
        for (int dx = -1; dx <= 1; dx++)
          {
          for (int dy = -1; dy <= 1; dy++)
            {
            // IF out of bounds
            if (x + dx < 0 || x + dx >= width || y + dy < 0 || y + dy >= height)
              {
              // next cell
              continue;
              }// end if
              
            // forget this flag with this cell
            grid[x + dx][y + dy].forgetSurroundingFlag();
            }// end for dy
          }// end for dx
        }// end if
      }
    catch (ArrayIndexOutOfBoundsException e)
      {
      /* grid[x][y] will throw an ArrayIndexOutOfBoundsException if x or y is
       * out of bounds. */
      
      // convert this exception to IllegalArgumentException
      throw new IllegalArgumentException("Arguments out of range.");
      }// end try..catch
      
    }// end setFlag(boolean, int, int)
    
  public void setFlag(boolean flagState, Point location)
                                                        throws IllegalArgumentException
    {
    setFlag(flagState, location.x, location.y);
    }// end setFlag(boolean, Point)
    
  public void setGameState(int state) throws IllegalArgumentException
    {
    // IF the given state is not valid
    if (!(DISABLED <= state && state <= WON))
      {
      throw new IllegalArgumentException("Invalid game state.");
      }// end if
      
    // set the game state
    gameState = state;
    }// end setGameState()
    
  public void setQuestion(boolean questionState, int x, int y)
                                                              throws IllegalArgumentException
    {
    try
      {
      grid[x][y].setQuestionState(questionState);
      }
    catch (ArrayIndexOutOfBoundsException e)
      {
      /* grid[x][y] will throw an ArrayIndexOutOfBoundsException if x or y is
       * out of bounds. */
      
      // convert this exception to IllegalArgumentException
      throw new IllegalArgumentException("Arguments out of range.");
      }// end try..catch
      
    }// end setFlag(boolean, int, int)
    
  public void setQuestion(boolean questionState, Point location)
                                                                throws IllegalArgumentException
    {
    setQuestion(questionState, location.x, location.y);
    }// end setFlag(boolean, Point)
    
  public void setQuestionState(boolean questionState, int x, int y)
    {
    grid[x][y].setQuestionState(questionState);
    }// end setQuestionState(boolean, int, int)
    
  public void setQuestionState(boolean questionState, Point cell)
    {
    setQuestionState(questionState, cell.x, cell.y);
    }// end setQuestionState(boolean, Point)
    
  public void setQuestionsUsed(boolean useQuestions)
    {
    this.useQuestions = useQuestions;
    }// end setIsQuestionUsed()
    
  public boolean setRevealState(int x, int y, boolean isRevealed)
    {
    grid[x][y].setRevealState(isRevealed);
    
    return grid[x][y].isSafe();
    }// end setRevealState(int,int,boolean)
    
  public boolean setRevealState(Point cell, boolean isRevealed)
    {
    return setRevealState(cell.x, cell.y, isRevealed);
    }// end setRevealState(Point,boolean)
    
  public void setUseQuestions(boolean state)
    {
    useQuestions = state;
    }// end setUseQuestions()
    
  /**
   * Toggles the mark of the cell. The marks will toggle in this order (assuming
   * an initial state of {@link #Cell Cell.DEFAULT}):
   * <ol>
   * <li>Flagged</li>
   * <li>Questioned</li>
   * <li>None</li>
   * <li>... and back to flagged.</li>
   * </ol>
   * @param x the x-coordinate of the cell
   * @param y the y-coordinate of the cell
   * @return true if the cell is now flagged; false otherwise
   */
  public boolean toggleMark(int x, int y)
    {
    Cell cell = grid[x][y];
    
    // IF this cell is flagged
    if (cell.getState() == Cell.FLAGGED)
      {
      // remove the flag
      setFlag(false, x, y);
      
      // IF questioning is enabled
      if (useQuestions)
        {
        // mark this cell with a '?'
        setQuestion(true, x, y);
        }// end if
        
        
      return false;
      }// [end if cell is flagged
      
    // ELSEIF this cell is marked with a question and questioning is enabled
    else if (cell.getState() == Cell.QUESTIONED && useQuestions)
      {
      // remove the question
      setQuestion(false, x, y);
      
      return false;
      }// [end if cell is questioned
      
    // ELSEIF this cell is blank
    else if (cell.getState() == Cell.DEFAULT)
      {
      // flag this cell
      setFlag(true, x, y);
      
      
      return true;
      }// [end if cell is blank
      
    else
      {
      // do nothing
      return false;
      }// end if
      
    }// end toggleMark(int,int)
    
  public boolean toggleMark(Point cell)
    {
    return toggleMark(cell.x, cell.y);
    }// end toggleMark(Point)
    
  public String toString()
    {
    String string = "";
    
    // DO for each cell
    for (Cell[] column : grid)
      {
      for (Cell cell : column)
        {
        // add this cell display character to the string
        string += cell.getDisplayChar();
        }// end for cell:column
        
      // add a newline
      string += "\n";
      }// end for grid
      
    return string;
    }// end toString()
    
  protected void initializeGrid()
    {
    // DO for each space in the grid
    for (int x = 0; x < grid.length; x++)
      {
      
      for (int y = 0; y < grid[x].length; y++)
        {
        grid[x][y] = new Cell();
        }// end for y
        
      }// end for x
      
    placeMinesRandomly();
    registerAllMines();
    }// end initializeGrid()
    
  }// end Minefield
