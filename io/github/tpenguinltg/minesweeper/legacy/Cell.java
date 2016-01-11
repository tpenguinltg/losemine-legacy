package io.github.tpenguinltg.minesweeper.legacy;

/**
 * A single space on a minesweeper grid.
 * @author tPenguinLTG
 * @version 1.0<br>
 *          Created 12 Jan 2012<br>
 *          Modified 22 Jan 2012
 */
public class Cell
  {
  private boolean isMine, isFlagged, isRevealed, isDetonated, isQuestioned;
  private int flagsAround, flagId, minesAround, mineId;
  
  public static final int DEFAULT = 0;
  public static final int FLAGGED = 1;
  public static final int QUESTIONED = 2;
  public static final int MINE = 3;
  public static final int DETONATED = 4;
  public static final int FALSE_FLAG = 5;
  public static final int ZERO = 6;
  public static final int ONE = 7;
  public static final int TWO = 8;
  public static final int THREE = 9;
  public static final int FOUR = 10;
  public static final int FIVE = 11;
  public static final int SIX = 12;
  public static final int SEVEN = 13;
  public static final int EIGHT = 14;
  public static final int UNKNOWN = 15;


  public Cell()
    {
    this(false, false, false, 0, -1, 0, -1);
    } // end constructor Cell()

  public Cell(boolean hasMine, boolean flagged, boolean revealed,
              int flagsAround, int flagId, int minesAround, int mineId)
    {
    this(hasMine, flagged, revealed, flagsAround, flagId, minesAround, mineId,
      false);
    } // end constructor Cell(boolean,boolean,boolean,int,int,int,int)

  public Cell(boolean hasMine, boolean flagged, boolean revealed,
              int flagsAround, int flagId, int minesAround, int mineId,
              boolean detonated)
    {
    this.isMine = hasMine;
    this.isFlagged = flagged;
    this.isRevealed = revealed;
    this.flagsAround = flagsAround;
    this.flagId = flagId;
    this.minesAround = minesAround;
    this.mineId = mineId;
    this.isDetonated = detonated;
    this.isQuestioned = false;
    } // end constructor Cell(boolean,boolean,boolean,int,int,int,int,boolean)
    
  public boolean addMine(int newMineId)
    {
    // IF this space does not already have a mine
    if (!isMine)
      {
      // register the mine
      isMine = true;
      mineId = newMineId;
      return true;
      }// end if
    else
      // there is already a mine
      {
      return false;
      }// end if
    } // end addMine()
    
  /**
   * Reveals and detonates the mine in this cell, if a mine exists.
   * @return true if detonation is successful (if there is a mine), false
   *         otherwise.
   */
  public boolean detonate()
    {
    if (isMine)
      {
      isDetonated = true;
      isRevealed = true;
      return true;
      }
    return false;
    }// end detonate()
    
  public void forgetSurroundingFlag()
    {
    flagsAround--;
    }// end forgetSurroundingFlag()
    
  public void forgetSurroundingMine()
    {
    minesAround--;
    }// end forgetSurroundingMine()
    
  public char getDisplayChar()
    {
    switch (getState())
      {
      case DEFAULT:
        return '~';
      case FLAGGED:
        return 'F';
      case QUESTIONED:
        return '?';
      case MINE:
        return '*';
      case DETONATED:
        return '@';
      case FALSE_FLAG:
        return 'X';
      case ZERO:
        return '0';
      case ONE:
        return '1';
      case TWO:
        return '2';
      case THREE:
        return '3';
      case FOUR:
        return '4';
      case FIVE:
        return '5';
      case SIX:
        return '6';
      case SEVEN:
        return '7';
      case EIGHT:
        return '8';
      default:
        return ' ';
      }// end switch
    }// end getDisplayChar()
    
  public int getFlagId()
    {
    return flagId;
    }// end getFlagId()
    
  public int getMineId()
    {
    return mineId;
    }// end getMineId()
    
  public int getState()
    {
    if (!isRevealed)
      {
      if (isFlagged)
        {
        return FLAGGED;
        }
      else if (isQuestioned)
        {
        return QUESTIONED;
        }
      else
        // regular
        {
        return DEFAULT;
        }// end if
      }// [end if not revealed
      
    // ELSE revealed
    
    else if (isDetonated)
      {
      return DETONATED;
      }
    else if (isMine)
      {
      return MINE;
      }
    else if (isFlagged)
      {
      return FALSE_FLAG;
      }
    else
      {
      switch (minesAround)
        {
        case 0:
          return ZERO;
        case 1:
          return ONE;
        case 2:
          return TWO;
        case 3:
          return THREE;
        case 4:
          return FOUR;
        case 5:
          return FIVE;
        case 6:
          return SIX;
        case 7:
          return SEVEN;
        case 8:
          return EIGHT;
        default:
          return UNKNOWN;
        }// end switch
      }// end if
    }// end getState()
    
  public int getSurroundingFlagCount()
    {
    return flagsAround;
    }// end getSurroundingFlagCount()
    
  public int getSurroundingMineCount()
    {
    return minesAround;
    }// end getSurroundingMineCount()
    
  public boolean isFlagged()
    {
    return isFlagged;
    }// end isFlagged()
    
  public boolean isMine()
    {
    return isMine;
    }// end isMine()
    
  public boolean isQuestioned()
    {
    return isQuestioned;
    }// end isQuestioned()
    
  public boolean isRevealed()
    {
    return isRevealed;
    }// end isRevealed()
    
  /**
   * A convenience method for <code>!isMine()</code>.
   * @return the opposite of isMine
   */
  public boolean isSafe()
    {
    return !isMine;
    }// end isSafe()
    
  public void registerSurroundingFlag()
    {
    flagsAround++;
    }// end registerSurroundingFlag()
    
  public void registerSurroundingMine()
    {
    minesAround++;
    }// end registerSurroundingMine()
    
  public void removeMine()
    {
    isMine = false;
    mineId = -1;
    } // end removeMine()
    
  public void setFlagId(int newFlagId)
    {
    flagId = newFlagId;
    }// end setFlagId()
    
  public void setFlagsAround(int surroundingFlagCount)
    {
    flagsAround = surroundingFlagCount;
    }// end setFlagsAround()
    
  public void setFlagState(boolean setFlagTo)
    {
    isFlagged = setFlagTo;
    }// end setFlagState()
    
  public void setMineId(int newMineId)
    {
    mineId = newMineId;
    }// end setMineId()
    
  public void setMinesAround(int surroundingMineCount)
    {
    minesAround = surroundingMineCount;
    }// end setMinesAround()
    
  public void setQuestionState(boolean setQTo)
    {
    isQuestioned = setQTo;
    }// end setQuestionState()
    
  public void setRevealState(boolean reveal)
    {
    isRevealed = reveal;
    }// end setIsRevealed
    
  public boolean toggleFlagState()
    {
    return isFlagged = !isFlagged;
    } // end toggleFlagState()
    
  public boolean toggleQuestionState()
    {
    return isQuestioned = !isQuestioned;
    }// end toggleQuestionState()
  } // end GridSpace
