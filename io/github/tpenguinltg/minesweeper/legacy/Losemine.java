package io.github.tpenguinltg.minesweeper.legacy;

import java.awt.*;
import java.awt.event.*;

import java.io.*;
import java.net.MalformedURLException;

import java.text.NumberFormat;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
//import java.util.Calendar;
//import java.util.Preferences;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;

/**
 * Losemine: A Windows XP Minesweeper ("Winmine") clone; a component of
 * Losedoze.
 * @author tPenguinLTG
 * @version 0.2<br>
 *          Created 12 Jan 2012<br>
 *          Modified 26 Jan 2012
 */
public class Losemine implements ActionListener, MouseListener, KeyListener
  {
  public static ImageIcon icon_favicon, icon_favicon_color, icon_favicon_bw;
  public static ImageIcon[] icon_cell, icon_cell_color, icon_cell_bw,
    icon_face, icon_face_color, icon_face_bw, icon_numbers, icon_numbers_color,
    icon_numbers_bw;
  
  public static final String VERSION = "0.2";
  
  // face state constants
  public static final int DEFAULT = 0;
  public static final int PRESSED = 1;
  public static final int HOLD_BREATH = 2;
  public static final int LOSE = 3;
  public static final int WIN = 4;
  
  // Game Menu constants
  public static final int NEW = 0;
  public static final int BEGINNER = 1;
  public static final int INTERMEDIATE = 2;
  public static final int EXPERT = 3;
  public static final int CUSTOM = 4;
  public static final int MARKS = 5;
  public static final int COLOR = 6;
  public static final int SOUND = 7;
  public static final int BEST_TIMES = 8;
  public static final int EXIT = 9;
  
  
  int height, width, mineCount;
  String difficulty;
  
  Minefield grid;
  SecondTimer timer;
  
  LinkedList<Score> highScores;
  Score[][] sortedScores;
  
  boolean isLeftMouseButtonDown, isMiddleMouseButtonDown,
    isRightMouseButtonDown;
  
  Object mouseOverObject;
  
  
  JFrame frame;
  JWindow xyzzyPixel; // not implemented yet
  JDialog dialog_customField, dialog_scores, dialog_help;
  JPanel rootPanel, gridPanel, infoPanel, flagsLeftPanel, timerPanel;
  JPanel[] customPanel, scoresPanel;
  
  JLabel lbl_height, lbl_width, lbl_mines;
  JLabel[] lbl_flagsLeft, lbl_timer;
  JLabel[][] lbl_scores, lbl_custom;
  
  JTextField txt_height, txt_width, txt_mines;
  
  JEditorPane edit_help;
  JScrollPane scroll_help;
  
  
  JButton btn_face, btn_customOk, btn_customCancel, btn_scoresReset,
    btn_scoresOk;
  
  JMenuBar menuBar;
  JMenu menuGame, menuHelp;
  JMenuItem[] mGameItems, mHelpItems;
  ButtonGroup difficultySelection;
  JButton[][] btns_grid;
  

  public Losemine()
    {
    setLookAndFeel();
    
    try
      {
      this.highScores = getScores();
      this.sortedScores = sortScores(highScores);
      }
    catch (Exception e)
      {
      // there are no scores to be read
      sortedScores = new Score[][] { {new Score("Beginner", 999, "Anonymous")},
        {new Score("Intermediate", 999, "Anonymous")},
        {new Score("Expert", 999, "Anonymous")}};
      }// end try..catch
      
    initializeImageIcons();
    initializeFields();
    initializePanels();
    initializeMenuBar();
    initializeFrames();
    assignListeners();
    
    /* //does not work properly just yet
     * try
     * {
     * getPreferences();
     * }
     * catch (Exception e)
     * {
     * ;// there are no preferences to read
     * } */
    
    // default preferences
    this.difficulty = "Beginner";
    this.height = 9;
    this.width = 9;
    this.mineCount = 10;
    
    timer = new SecondTimer();
    
    newGame();
    
    frame.setVisible(true);
    
    }// end constructor
    
  public class Score
    {
    protected int score;
    protected String difficulty, name;
    
    public Score(String difficulty, int score, String name)
      {
      this.difficulty = difficulty;
      this.score = score;
      this.name = name;
      }// end constructor
      
    public String getDifficulty()
      {
      return difficulty;
      }// end getDifficulty
      
    public String getName()
      {
      return name;
      }// end getName()
      
    public int getScore()
      {
      return score;
      }// end getScore()
      
    public void setDifficulty(String difficulty)
      {
      this.difficulty = difficulty;
      }// end setDifficulty()
      
    public void setName(String name)
      {
      this.name = name;
      }// end setName()
      
    public void setScore(int score)
      {
      this.score = score;
      }// end setScore()
      
    public String toString()
      {
      return difficulty + "," + score + "," + name;
      }// end toString()
      
    }// end Score
    
  public class SecondTimer
    {
    private int secsElapsed;
    private long timerStart;
    private boolean isTimerRunning;
    private Timer t;
    protected final NumberFormat format;
    
    public SecondTimer()
      {
      secsElapsed = 0;
      
      format = NumberFormat.getIntegerInstance();
      format.setMinimumIntegerDigits(lbl_timer.length);
      
      // initialize timer display
      updateTimerDisplay();
      
      t = new Timer();
      }// end constructor
      
    public int getsecsElapsed()
      {
      return secsElapsed;
      }
      
    public boolean isTimerRunning()
      {
      return isTimerRunning;
      }
      
    public void start()
      {
      secsElapsed = 0;
      timerStart = System.currentTimeMillis();
      t.schedule(new TimerTask()
        {
          public void run()
            {
            time();
            }// end run()
        }, 1L);
      }// end start()
      
    public void stop()
      {
      t.cancel();
      }// end stop()
      
    public void updateTimerDisplay()
      {
      String displayedTime = format.format(secsElapsed);
      
      
      // DO for each digit
      for (int digit = 0; digit < lbl_timer.length; digit++)
        {
        // display it
        int digitDisplayed = Integer.parseInt(displayedTime.charAt(digit) + "");
        lbl_timer[digit].setIcon(icon_numbers[digitDisplayed]);
        }// end for digit
      }// end updateTimerDisplay()
    
    protected void time()
      {
      secsElapsed = (int) Math
        .ceil((System.currentTimeMillis() - timerStart) / 1000.0);
      
      // cap timer at 999
      if (secsElapsed <= 999)
        {
        updateTimerDisplay();
        
        t.schedule(new TimerTask()
          {
            public void run()
              {
              time();
              }// end run()
          }, 1L);
        }// end if
      }// end time()
    }// end Timer
    
  public static void main(String[] args)
    {
    new Losemine();
    }// end main()
    
  public void actionPerformed(ActionEvent e)
    {
    
    Object source = e.getSource();
    
    // main frame
    if (source == btn_face || source == mGameItems[NEW])
      {
      newGame();
      }
    
    // game menu items
    else if (source == mGameItems[BEGINNER])
      {
      difficulty = "Beginner";
      width = 9;
      height = 9;
      mineCount = 10;
      newGame();
      }
    else if (source == mGameItems[INTERMEDIATE])
      {
      difficulty = "Intermediate";
      width = 16;
      height = 16;
      mineCount = 40;
      newGame();
      }
    else if (source == mGameItems[EXPERT])
      {
      difficulty = "Expert";
      width = 30;
      height = 16;
      mineCount = 99;
      newGame();
      }
    else if (source == mGameItems[CUSTOM])
      {
      difficulty = "Custom";
      
      // update text fields
      txt_width.setText(width + "");
      txt_height.setText(height + "");
      txt_mines.setText(mineCount + "");
      
      
      dialog_customField.setVisible(true);
      }
    else if (source == mGameItems[MARKS])
      {
      grid.setQuestionsUsed(mGameItems[MARKS].isSelected());
      }
    else if (source == mGameItems[COLOR])
      {
      updateFrame();
      }
    else if (source == mGameItems[BEST_TIMES])
      {
      try
        {
        showScores();
        }
      catch (IOException e1)
        {
        e1.printStackTrace();
        }
      }
    else if (source == mGameItems[EXIT])
      {
      System.exit(0);
      }
    
    // help menu items
    else if (source == mHelpItems[0])
      {
      dialog_help.setVisible(true);
      }
    else if (source == mHelpItems[1])
      {
      showAbout();
      }
    
    // custom dialog buttons
    else if (source == btn_customOk)
      {
      int newWidth, newHeight, newMineCount;
      
      // get width
      try
        {
        newWidth = Integer.parseInt(txt_width.getText());
        }
      catch (NumberFormatException e1)
        {
        // keep old width
        newWidth = width;
        }
      
      // get height
      try
        {
        newHeight = Integer.parseInt(txt_height.getText());
        }
      catch (NumberFormatException e1)
        {
        // keep old width
        newHeight = height;
        }
      
      // get mine count
      try
        {
        newMineCount = Integer.parseInt(txt_mines.getText());
        }
      catch (NumberFormatException e1)
        {
        // keep old width
        newMineCount = mineCount;
        }
      
      // normalize values
      // width
      if (newWidth < 9)
        {
        newWidth = 9;
        }
      else if (newWidth > 30)
        {
        newWidth = 30;
        }
      // height
      if (newHeight < 9)
        {
        newHeight = 9;
        }
      else if (newHeight > 24)
        {
        newHeight = 24;
        }
      // mine count
      if (newMineCount < 10)
        {
        newMineCount = 9;
        }
      else if (newMineCount > ((newWidth - 1) * (newHeight - 1)))
        {
        newMineCount = (newWidth - 1) * (newHeight - 1);
        }
      
      // use new values
      width = newWidth;
      height = newHeight;
      mineCount = newMineCount;
      
      // hide dialog
      dialog_customField.setVisible(false);
      
      newGame();
      }// [end custom dialog OK button
    else if (source == btn_customCancel)
      {
      // hide dialog
      dialog_customField.setVisible(false);
      
      newGame();
      }
    
    // scores dialog buttons
    else if (source == btn_scoresOk)
      {
      // hide dialog
      dialog_scores.setVisible(false);
      }
    else if (source == btn_scoresReset)
      {
      try
        {
        resetScores();
        }
      catch (IOException e1)
        {
        ;
        }
      }
    }// end actionPerformed()
    
  public void assignListeners()
    {
    // buttons
    btn_customOk.addActionListener(this);
    btn_customCancel.addActionListener(this);
    btn_scoresReset.addActionListener(this);
    btn_scoresOk.addActionListener(this);
    btn_face.addActionListener(this);
    
    // menu items
    for (JMenuItem item : mGameItems)
    // for (MenuItem item : (MenuItem[]) menuGame.getSubElements())
      {
      item.addActionListener(this);
      }// end for item:mGameItems
    for (JMenuItem item : mHelpItems)
      {
      item.addActionListener(this);
      }// end for item:mHelpItems
      
    // easter egg (unimplemented)
    frame.addKeyListener(this);
    }// end assignListeners()
    
  public boolean checkAndDoWin()
    {
    // assume win
    boolean win = true;
    
    // DO for each cell
    for (int x = 0; x < width; x++)
      {
      for (int y = 0; y < height; y++)
        {
        // check for win
        // IF this cell is not revealed and is not a mine or is revealed and is
        // a mine
        if (!grid.isCellRevealed(x, y) && !grid.isMine(x, y) ||
          grid.isCellRevealed(x, y) && grid.isMine(x, y))
          {
          // not win
          win = false;
          }// end if
        }// end for y
      }// end for x
      
    if (win)
      {
      try
        {
        win();
        }
      catch (IOException e)
        {
        ;
        }
      }
    
    return win;
    }// end checkAndDoWin()
    
  public void initializeFields()
    {
    // flags left labels
    lbl_flagsLeft = new JLabel[3];
    for (int i = 0; i < lbl_flagsLeft.length; i++)
      {
      lbl_flagsLeft[i] = new JLabel(icon_numbers[11]);
      }// end for
      
    // face button
    btn_face = new JButton(icon_face[DEFAULT]);
    btn_face.setPressedIcon(icon_face[PRESSED]);
    btn_face.setBackground(new Color(0xC0, 0xC0, 0xC0));
    btn_face.setBorder(BorderFactory.createLineBorder(new Color(0x80, 0x80,
      0x80), 1));
    btn_face.setFocusable(false);
    
    // timer labels
    lbl_timer = new JLabel[3];
    for (int i = 0; i < lbl_timer.length; i++)
      {
      lbl_timer[i] = new JLabel(icon_numbers[11]);
      }// end for
      
    // scores dialog labels
    lbl_scores = new JLabel[3][3];
    lbl_scores[0][0] = new JLabel("Beginner:");
    lbl_scores[0][1] = new JLabel("999 seconds");
    lbl_scores[0][2] = new JLabel("Anonymous");
    lbl_scores[1][0] = new JLabel("Intermediate:");
    lbl_scores[1][1] = new JLabel("999 seconds");
    lbl_scores[1][2] = new JLabel("Anonymous");
    lbl_scores[2][0] = new JLabel("Expert:");
    lbl_scores[2][1] = new JLabel("999 seconds");
    lbl_scores[2][2] = new JLabel("Anonymous");
    
    // scores dialog buttons
    btn_scoresOk = new JButton("OK");
    btn_scoresReset = new JButton("Reset Scores");
    btn_scoresReset.setMnemonic(KeyEvent.VK_R);
    
    // custom field dialog text fields
    txt_height = new JTextField("9", 5);
    txt_width = new JTextField("9", 5);
    txt_mines = new JTextField("10", 5);
    
    // custom field dialog labels
    lbl_height = new JLabel("Height:");
    lbl_height.setLabelFor(txt_height);
    lbl_width = new JLabel("Width:");
    lbl_width.setLabelFor(txt_width);
    lbl_mines = new JLabel("Mines:");
    lbl_mines.setLabelFor(txt_mines);
    
    // custom field dialog buttons
    btn_customOk = new JButton("OK");
    btn_customCancel = new JButton("Cancel");
    
    // help dialog text pane
    edit_help = new JEditorPane("text/plain",
      "X Y Z Z Y [Shift]\nMissing help file.");
    
    //File helpFile = new File("help.htm");
    
    // IF the help file exists
    //if (helpFile.isFile())
      {
      try
        {
        // use it instead
        //edit_help = new JEditorPane(helpFile.toURI().toURL());
        edit_help = new JEditorPane(getClass().getResource("/io/github/tpenguinltg/minesweeper/legacy/help.htm"));
        }
      catch (Exception e)
        {
        ;
        }
      }
    
    edit_help.setEditable(false);
    
    
    // add the editor to a scroll pane
    scroll_help = new JScrollPane(edit_help);
    scroll_help.setBorder(BorderFactory.createLoweredBevelBorder());
    scroll_help.setPreferredSize(new Dimension(320, 360));
    
    }// end initializeFields
    
  public void initializeFrames()
    {
    Container c;
    
    frame = new JFrame("Minesweeper");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setBackground(new Color(0xC0, 0xC0, 0xC0));
    frame.setIconImage(icon_favicon.getImage());
    frame.setResizable(false);
    frame.setLocationRelativeTo(null);
    JFrame.setDefaultLookAndFeelDecorated(true);
    
    // add root panel
    c = frame.getContentPane();
    c.setLayout(new GridLayout(1, 1));
    c.add(rootPanel);
    
    // add menu bar
    frame.setJMenuBar(menuBar);
    
    
    // custom field dialog //
    dialog_customField = new JDialog(frame, "Custom Field", true);
    
    // add elements to dialog
    c = dialog_customField.getContentPane();
    c.add(customPanel[0], BorderLayout.WEST);
    c.add(customPanel[1], BorderLayout.EAST);
    
    dialog_customField.getRootPane().setDefaultButton(btn_customOk);
    
    dialog_customField.pack();
    
    // end custom field dialog //
    
    // scores dialog //
    dialog_scores = new JDialog(frame, "Fastest Mine Sweepers", true);
    
    // add elements to dialog
    c = dialog_scores.getContentPane();
    c.add(scoresPanel[0], BorderLayout.CENTER);
    c.add(scoresPanel[1], BorderLayout.SOUTH);
    
    dialog_customField.getRootPane().setDefaultButton(btn_scoresOk);
    
    dialog_scores.pack();
    
    // end scores dialog //
    
    // help dialog //
    dialog_help = new JDialog(frame, "Minesweeper", false);
    c = dialog_help.getContentPane();
    c.add(scroll_help);
    dialog_help.pack();
    // end help dialog //
    
    // easter egg (xyzzy) pixel //
    xyzzyPixel = new JWindow(frame);
    xyzzyPixel.setSize(1, 1);
    xyzzyPixel.setBackground(Color.WHITE);
    // end xyzzy pixel //
    }// end initializeFrame()
    
  public void initializeImageIcons()
    {
    // window icons
    icon_favicon_color = new ImageIcon(getClass().getResource("/io/github/tpenguinltg/minesweeper/legacy/images/color/favicon.gif"));
    icon_favicon_bw = new ImageIcon(getClass().getResource("/io/github/tpenguinltg/minesweeper/legacy/images/bw/favicon.gif"));
    icon_favicon = icon_favicon_color;
    
    
    // Color icons //
    
    // cell icons
    icon_cell_color = new ImageIcon[16];
    for (int i = 0; i < icon_cell_color.length; i++)
      {
      icon_cell_color[i] = new ImageIcon(getClass().getResource("/io/github/tpenguinltg/minesweeper/legacy/images/color/cell-" + i +
        ".gif"));
      }// end for
      
    // face icons
    icon_face_color = new ImageIcon[5];
    icon_face_color[DEFAULT] = new ImageIcon(
      getClass().getResource("/io/github/tpenguinltg/minesweeper/legacy/images/color/face-default.gif"));
    icon_face_color[PRESSED] = new ImageIcon(
      getClass().getResource("/io/github/tpenguinltg/minesweeper/legacy/images/color/face-pressed.gif"));
    icon_face_color[HOLD_BREATH] = new ImageIcon(
      getClass().getResource("/io/github/tpenguinltg/minesweeper/legacy/images/color/face-hold_breath.gif"));
    icon_face_color[LOSE] = new ImageIcon(
      getClass().getResource("/io/github/tpenguinltg/minesweeper/legacy/images/color/face-lose.gif"));
    icon_face_color[WIN] = new ImageIcon(
      getClass().getResource("/io/github/tpenguinltg/minesweeper/legacy/images/color/face-win.gif"));
    
    // number icons
    icon_numbers_color = new ImageIcon[12];
    for (int i = 0; i < 10; i++)
      {
      icon_numbers_color[i] = new ImageIcon(
        getClass().getResource("/io/github/tpenguinltg/minesweeper/legacy/images/color/numbers-" + i + ".gif"));
      }// end for
    icon_numbers_color[10] = new ImageIcon(
      getClass().getResource("/io/github/tpenguinltg/minesweeper/legacy/images/color/numbers--.gif"));
    icon_numbers_color[11] = new ImageIcon(
      getClass().getResource("/io/github/tpenguinltg/minesweeper/legacy/images/color/numbers-u.gif"));
    
    // end color icons //
    
    // B&W icons //
    
    // cell icons
    icon_cell_bw = new ImageIcon[16];
    for (int i = 0; i < icon_cell_bw.length; i++)
      {
      icon_cell_bw[i] = new ImageIcon(getClass().getResource("/io/github/tpenguinltg/minesweeper/legacy/images/bw/cell-" + i +
        ".gif"));
      }// end for
      
    // face icons
    icon_face_bw = new ImageIcon[5];
    icon_face_bw[DEFAULT] = new ImageIcon(
      getClass().getResource("/io/github/tpenguinltg/minesweeper/legacy/images/bw/face-default.gif"));
    icon_face_bw[PRESSED] = new ImageIcon(
      getClass().getResource("/io/github/tpenguinltg/minesweeper/legacy/images/bw/face-pressed.gif"));
    icon_face_bw[HOLD_BREATH] = new ImageIcon(
      getClass().getResource("/io/github/tpenguinltg/minesweeper/legacy/images/bw/face-hold_breath.gif"));
    icon_face_bw[LOSE] = new ImageIcon(getClass().getResource("/io/github/tpenguinltg/minesweeper/legacy/images/bw/face-lose.gif"));
    icon_face_bw[WIN] = new ImageIcon(getClass().getResource("/io/github/tpenguinltg/minesweeper/legacy/images/bw/face-win.gif"));
    
    // number icons
    icon_numbers_bw = new ImageIcon[12];
    for (int i = 0; i < 10; i++)
      {
      icon_numbers_bw[i] = new ImageIcon(getClass().getResource("/io/github/tpenguinltg/minesweeper/legacy/images/bw/numbers-" + i +
        ".gif"));
      }// end for
    icon_numbers_bw[10] = new ImageIcon(getClass().getResource("/io/github/tpenguinltg/minesweeper/legacy/images/bw/numbers--.gif"));
    icon_numbers_bw[11] = new ImageIcon(getClass().getResource("/io/github/tpenguinltg/minesweeper/legacy/images/bw/numbers-u.gif"));
    
    // end B&W icons //
    
    // by default, use the color icons
    icon_cell = icon_cell_color;
    icon_face = icon_face_color;
    icon_numbers = icon_numbers_color;
    
    }// end initializeImageIcons()
    
  public void initializeMenuBar()
    {
    // Game menu //
    mGameItems = new JMenuItem[10];
    mGameItems[NEW] = new JMenuItem("New", KeyEvent.VK_N);
    mGameItems[BEGINNER] = new JRadioButtonMenuItem("Beginner", true);
    mGameItems[INTERMEDIATE] = new JRadioButtonMenuItem("Intermediate", false);
    mGameItems[EXPERT] = new JRadioButtonMenuItem("Expert", false);
    mGameItems[CUSTOM] = new JRadioButtonMenuItem("Custom...", false);
    mGameItems[MARKS] = new JCheckBoxMenuItem("Marks (?)", true);
    mGameItems[COLOR] = new JCheckBoxMenuItem("Color", true);
    mGameItems[SOUND] = new JCheckBoxMenuItem("Sound", false);
    mGameItems[BEST_TIMES] = new JMenuItem("Best Times...", KeyEvent.VK_T);
    mGameItems[EXIT] = new JMenuItem("Exit", KeyEvent.VK_X);
    
    // add difficulty selection in a button group
    difficultySelection = new ButtonGroup();
    difficultySelection.add(mGameItems[BEGINNER]);
    difficultySelection.add(mGameItems[INTERMEDIATE]);
    difficultySelection.add(mGameItems[EXPERT]);
    difficultySelection.add(mGameItems[CUSTOM]);
    
    mGameItems[NEW].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0,
                                                          false));
    
    // assign mnemonics
    mGameItems[BEGINNER].setMnemonic(KeyEvent.VK_B);
    mGameItems[INTERMEDIATE].setMnemonic(KeyEvent.VK_I);
    mGameItems[EXPERT].setMnemonic(KeyEvent.VK_E);
    mGameItems[CUSTOM].setMnemonic(KeyEvent.VK_C);
    mGameItems[MARKS].setMnemonic(KeyEvent.VK_M);
    mGameItems[COLOR].setMnemonic(KeyEvent.VK_L);
    mGameItems[SOUND].setMnemonic(KeyEvent.VK_S);
    
    // disable buggy or (yet) unimplemented functions
    //mGameItems[EXPERT].setEnabled(false);
    //mGameItems[CUSTOM].setEnabled(false);
    mGameItems[SOUND].setEnabled(false);
    
    
    
    menuGame = new JMenu("Game");
    menuGame.setMnemonic(KeyEvent.VK_G);
    
    // add game menu items
    for (JMenuItem item : mGameItems)
      {
      menuGame.add(item);
      }// end for
      
    // add separators (in reverse order for simplicity)
    menuGame.insertSeparator(9);
    menuGame.insertSeparator(8);
    menuGame.insertSeparator(5);
    menuGame.insertSeparator(1);
    
    // end game menu //
    
    // Help menu //
    mHelpItems = new JMenuItem[2];
    mHelpItems[0] = new JMenuItem("Contents", KeyEvent.VK_C);
    mHelpItems[1] = new JMenuItem("About Minesweeper...", KeyEvent.VK_A);
    
    mHelpItems[0].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0,
                                                        false));
    
    menuHelp = new JMenu("Help");
    menuHelp.setMnemonic(KeyEvent.VK_H);
    
    // add items
    for (JMenuItem item : mHelpItems)
      {
      menuHelp.add(item);
      }// end for
      
    // add separator
    menuHelp.insertSeparator(1);
    
    // end help menu //
    
    // menu bar //
    menuBar = new JMenuBar();
    menuBar.setBorderPainted(false);
    
    // add menus
    menuBar.add(menuGame);
    menuBar.add(menuHelp);
    
    // end menu bar //
    }// end initializeMenuBar()
    
  public void initializePanels()
    {
    GridBagConstraints gc = new GridBagConstraints();
    
    // flags left panel
    flagsLeftPanel = new JPanel(new GridLayout(1, 3));
    for (JLabel image : lbl_flagsLeft)
      {
      flagsLeftPanel.add(image);
      }// end for image:lbl_flagsLeft
    flagsLeftPanel.setBorder(BorderFactory.createLoweredBevelBorder());
    
    
    // timer panel
    timerPanel = new JPanel(new GridLayout(1, 3));
    for (JLabel image : lbl_timer)
      {
      timerPanel.add(image);
      }// end for image:lbl_timer
    timerPanel.setBorder(BorderFactory.createLoweredBevelBorder());
    
    // information panel //
    infoPanel = new JPanel(new GridBagLayout());
    infoPanel
      .setBorder(BorderFactory.createCompoundBorder(BorderFactory
        .createLoweredBevelBorder(), BorderFactory
        .createEmptyBorder(4, 5, 3, 7)));
    
    // flags left panel
    gc.gridx = 0;
    gc.gridy = 0;
    gc.weightx = 1.0;
    gc.weighty = 0.0;
    gc.ipadx = 0;
    gc.ipady = 0;
    gc.fill = GridBagConstraints.NONE;
    gc.anchor = GridBagConstraints.WEST;
    infoPanel.add(flagsLeftPanel, gc);
    
    // face
    gc.gridx = 1;
    gc.gridy = 0;
    gc.weightx = 0.0;
    gc.weighty = 0.0;
    gc.ipadx = 0;
    gc.ipady = 0;
    gc.fill = GridBagConstraints.NONE;
    gc.anchor = GridBagConstraints.CENTER;
    infoPanel.add(btn_face, gc);
    
    // timer
    gc.gridx = 2;
    gc.gridy = 0;
    gc.weightx = 1.0;
    gc.weighty = 0.0;
    gc.ipadx = 0;
    gc.ipady = 0;
    gc.fill = GridBagConstraints.NONE;
    gc.anchor = GridBagConstraints.EAST;
    infoPanel.add(timerPanel, gc);
    
    infoPanel.setBackground(new Color(0xC0, 0xC0, 0xC0));
    
    // end information panel //
    
    // grid panel //
    gridPanel = new JPanel();
    gridPanel.setBackground(new Color(0xC0, 0xC0, 0xC0));
    gridPanel
      .setBorder(BorderFactory.createCompoundBorder(BorderFactory
        .createEmptyBorder(6, 0, 0, 0), BorderFactory
        .createLoweredBevelBorder()));
    
    // end grid panel //
    
    // custom field dialog panels //
    customPanel = new JPanel[2];
    
    // input panel
    customPanel[0] = new JPanel(new GridLayout(3, 2));
    // add elements to this panel
    customPanel[0].add(lbl_height);
    customPanel[0].add(txt_height);
    customPanel[0].add(lbl_width);
    customPanel[0].add(txt_width);
    customPanel[0].add(lbl_mines);
    customPanel[0].add(txt_mines);
    
    // button panel
    customPanel[1] = new JPanel(new GridLayout(2, 1, 4, 0));
    // add elements to this panel
    customPanel[1].add(btn_customOk);
    customPanel[1].add(btn_customCancel);
    
    // end custom field dialog panel //
    
    // scores dialog panel //
    scoresPanel = new JPanel[2];
    
    // scores panel
    scoresPanel[0] = new JPanel(new GridLayout(3, 3, 22, 6));
    scoresPanel[0].setBorder(BorderFactory.createEmptyBorder(17, 17, 17, 17));
    // add elements to this panel
    for (JLabel[] row : lbl_scores)
      {
      for (JLabel label : row)
        {
        scoresPanel[0].add(label);
        }// end for label:row
      }// end for row:lbl_scores
      
    // button panel
    scoresPanel[1] = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    // add elements to this panel
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    scoresPanel[1].add(btn_scoresReset, gbc);
    
    gbc.gridx = 1;
    scoresPanel[1].add(btn_scoresOk, gbc);
    
    // end scores dialog panel//
    
    // root panel
    rootPanel = new JPanel(new GridBagLayout(), true);
    rootPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
      .createMatteBorder(3, 3, 0, 0, Color.WHITE), BorderFactory
      .createEmptyBorder(6, 6, 5, 5)));
    
    // reset constraints
    gc = new GridBagConstraints();
    
    // information panel
    gc.gridx = 0;
    gc.gridy = 0;
    gc.fill = GridBagConstraints.HORIZONTAL;
    rootPanel.add(infoPanel, gc);
    
    // grid panel
    gc.gridx = 0;
    gc.gridy = 1;
    rootPanel.add(gridPanel, gc);
    
    rootPanel.setBackground(new Color(0xC0, 0xC0, 0xC0));
    rootPanel.addMouseListener(this);
    // end root panel //
    
    }// end initializePanels()
    
  public boolean isHighScore(Score score) throws IOException
    {
    updateScores();
    
    /* ****************************
     * SEARCH ALGORITHM
     * **************************** */
    
    // grab the first element of the appropriate sorted list
    // (should be lowest time)
    if (score.getDifficulty().equalsIgnoreCase("Beginner"))
      {
      // compare scores (this score greater than highest recorded score)
      return score.getScore() < sortedScores[0][0].getScore();
      }
    else if (score.getDifficulty().equalsIgnoreCase("Intermediate"))
      {
      // compare scores (this score greater than highest recorded score)
      return score.getScore() < sortedScores[1][0].getScore();
      }
    else if (score.getDifficulty().equalsIgnoreCase("Expert"))
      {
      // compare scores (this score greater than highest recorded score)
      return score.getScore() < sortedScores[2][0].getScore();
      }
    else
      {
      return false;
      }// end if
      
    }// end isHighScore()
    
  public void keyPressed(KeyEvent e)
    {
    
    }// end keyPressed()
    
  public void keyReleased(KeyEvent e)
    {
    
    }// end keyReleased()
    
  public void keyTyped(KeyEvent e)
    {
    
    }// end keyTyped()
    
  public void lose()
    {
    timer.stop();
    btn_face.setIcon(icon_face[LOSE]);
    grid.loseReveal();
    updateGrid();
    setComponentEnabled(false, gridPanel);
    }// end lose()
    
  public void mouseClicked(MouseEvent e)
    {
    
    }// end mouseClicked()
    
  public void mouseEntered(MouseEvent e)
    {
    mouseOverObject = e.getSource();
    
    if (isLeftMouseButtonDown)
      {
      // check source
      gridLoop:
      for (int x = 0; x < width; x++)
        {
        for (int y = 0; y < height; y++)
          {
          // IF it is one of the buttons
          if (mouseOverObject == btns_grid[x][y] && btns_grid[x][y].isEnabled())
            {
            // appear pressed
            btns_grid[x][y].setIcon(btns_grid[x][y].getPressedIcon());
            break gridLoop;
            }// end if
          }// end for y
        }// end for x
      }
    else if (isMiddleMouseButtonDown)
      {
      // check source
      gridLoop:
      for (int x = 0; x < width; x++)
        {
        for (int y = 0; y < height; y++)
          {
          // IF it is one of the buttons
          if (mouseOverObject == btns_grid[x][y] && btns_grid[x][y].isEnabled())
            {
            
            // DO for each surrounding cell
            for (int dx = -1; dx <= 1; dx++)
              {
              for (int dy = -1; dy <= 1; dy++)
                {
                
                // IF out of grid bounds
                if (x + dx < 0 || x + dx >= width || y + dy < 0 ||
                  y + dy >= height)
                  {
                  continue;
                  }// end if
                  
                // appear pressed
                btns_grid[x + dx][y + dy].setIcon(btns_grid[x + dx][y + dy]
                  .getPressedIcon());
                }// end for dy
              }// end for dx
              
              
            break gridLoop;
            
            }// end if
          }// end for y
        }// end for x
      }// end if
    }// end mouseEntered()
    
  public void mouseExited(MouseEvent e)
    {
    
    if (isLeftMouseButtonDown)
      {
      // check source
      gridLoop:
      for (int x = 0; x < width; x++)
        {
        for (int y = 0; y < height; y++)
          {
          // IF it is one of the buttons
          if (mouseOverObject == btns_grid[x][y] && btns_grid[x][y].isEnabled())
            {
            // appear not pressed
            btns_grid[x][y].setIcon(icon_cell[grid.getCellState(x, y)]);
            break gridLoop;
            }// end if
          }// end for y
        }// end for x
      }
    else if (isMiddleMouseButtonDown)
      {
      // check source
      gridLoop:
      for (int x = 0; x < width; x++)
        {
        for (int y = 0; y < height; y++)
          {
          // IF it is one of the buttons
          if (mouseOverObject == btns_grid[x][y] && btns_grid[x][y].isEnabled())
            {
            
            // DO for each surrounding cell
            for (int dx = -1; dx <= 1; dx++)
              {
              for (int dy = -1; dy <= 1; dy++)
                {
                
                // IF out of grid bounds
                if (x + dx < 0 || x + dx >= width || y + dy < 0 ||
                  y + dy >= height)
                  {
                  continue;
                  }// end if
                  
                // appear not pressed
                btns_grid[x + dx][y + dy].setIcon(icon_cell[grid
                  .getCellState(x + dx, y + dy)]);
                }// end for dy
              }// end for dx
              
              
            break gridLoop;
            
            }// end if
          }// end for y
        }// end for x
      }// end if
      
    mouseOverObject = null;
    }// end mousePressed()
    
  public void mousePressed(MouseEvent e)
    {
    if (SwingUtilities.isLeftMouseButton(e))
      {
      isLeftMouseButtonDown = true;
      
      // IF not end of game
      if (!(grid.getGameState() == Minefield.LOST || grid.getGameState() == Minefield.WON))
        {
        // change face icon to 'hold breath'
        btn_face.setIcon(icon_face[HOLD_BREATH]);
        }// end if
      }
    else if (SwingUtilities.isMiddleMouseButton(e))
      {
      isMiddleMouseButtonDown = true;
      
      // IF not end of game
      if (!(grid.getGameState() == Minefield.LOST || grid.getGameState() == Minefield.WON))
        {
        // change face icon to 'hold breath'
        btn_face.setIcon(icon_face[HOLD_BREATH]);
        }// end if
        
      // check source
      gridLoop:
      for (int x = 0; x < width; x++)
        {
        for (int y = 0; y < height; y++)
          {
          // IF it is one of the buttons
          if (mouseOverObject == btns_grid[x][y] && btns_grid[x][y].isEnabled())
            {
            
            // DO for each surrounding cell
            for (int dx = -1; dx <= 1; dx++)
              {
              for (int dy = -1; dy <= 1; dy++)
                {
                
                // IF out of grid bounds
                if (x + dx < 0 || x + dx >= width || y + dy < 0 ||
                  y + dy >= height)
                  {
                  continue;
                  }// end if
                  
                // appear pressed
                btns_grid[x + dx][y + dy].setIcon(btns_grid[x + dx][y + dy]
                  .getPressedIcon());
                }// end for dy
              }// end for dx
              
              
            break gridLoop;
            
            }// end if
          }// end for y
        }// end for x
      
      }
    else if (SwingUtilities.isRightMouseButton(e))
      {
      isRightMouseButtonDown = true;
      
      if (isLeftMouseButtonDown)
        {
        return;
        }// end if
        
      gridLoop:
      for (int x = 0; x < width; x++)
        {
        for (int y = 0; y < height; y++)
          {
          if (e.getSource() == btns_grid[x][y] && btns_grid[x][y].isEnabled())
            {
            toggleFlag(x, y);
            break gridLoop;
            }// end if
          }// end for y
        }// end for x
      }// end if
    }// end mousePressed()
    
  public void mouseReleased(MouseEvent e)
    {
    if (SwingUtilities.isLeftMouseButton(e))
      {
      isLeftMouseButtonDown = false;
      
      // IF not end of game
      if (!(grid.getGameState() == Minefield.LOST || grid.getGameState() == Minefield.WON))
        {
        // change face icon to 'hold breath'
        btn_face.setIcon(icon_face[DEFAULT]);
        }// end if
        
      // check source
      gridLoop:
      for (int x = 0; x < width; x++)
        {
        for (int y = 0; y < height; y++)
          {
          // IF the mouse is over one of the buttons
          if (mouseOverObject == btns_grid[x][y] && btns_grid[x][y].isEnabled())
            {
            revealCell(x, y);
            break gridLoop;
            }// end if
          }// end for y
        }// end for x
      }
    else if (SwingUtilities.isMiddleMouseButton(e))
      {
      isMiddleMouseButtonDown = false;
      
      // IF not end of game
      if (!(grid.getGameState() == Minefield.LOST || grid.getGameState() == Minefield.WON))
        {
        // change face icon to 'hold breath'
        btn_face.setIcon(icon_face[DEFAULT]);
        }// end if
        
      // check source
      gridLoop:
      for (int x = 0; x < width; x++)
        {
        for (int y = 0; y < height; y++)
          {
          // IF the mouse is over one of the buttons
          if (mouseOverObject == btns_grid[x][y] && btns_grid[x][y].isEnabled())
            {
            
            // DO for each surrounding cell
            for (int dx = -1; dx <= 1; dx++)
              {
              for (int dy = -1; dy <= 1; dy++)
                {
                
                // IF out of grid bounds
                if (x + dx < 0 || x + dx >= width || y + dy < 0 ||
                  y + dy >= height)
                  {
                  continue;
                  }// end if
                  
                // appear not pressed
                btns_grid[x + dx][y + dy].setIcon(icon_cell[grid
                  .getCellState(x + dx, y + dy)]);
                }// end for dy
              }// end for dx
              
            revealSurroundingCells(x, y);
            
            break gridLoop;
            }// end if
          }// end for y
        }// end for x
        
      }
    else if (SwingUtilities.isRightMouseButton(e))
      {
      isRightMouseButtonDown = false;
      
      if (isLeftMouseButtonDown)
        {
        // check source
        gridLoop:
        for (int x = 0; x < width; x++)
          {
          for (int y = 0; y < height; y++)
            {
            if (mouseOverObject == btns_grid[x][y] &&
              btns_grid[x][y].isEnabled())
              {
              revealSurroundingCells(x, y);
              
              break gridLoop;
              }// end if
            }// end for y
          }// end for x
        }// end if
      }// end if
    }// end mouseReleased()
    
  public void newGame()
    {
    grid = new Minefield(width, height, mineCount);
    btns_grid = new JButton[width][height];
    
    // stop any running timers
    timer.stop();
    
    // reset the timer
    timer = new SecondTimer();
    
    // reset the flags left display
    updateFlagsLeftDisplay();
    
    // reset the grid panel
    gridPanel.removeAll();
    gridPanel.setLayout(new GridLayout(height, width));
    setComponentEnabled(true, gridPanel);
    
    // DO for each button
    for (int y = 0; y < height; y++)
      {
      for (int x = 0; x < width; x++)
        {
        // initialize it, showing the default icon
        btns_grid[x][y] = new JButton(icon_cell[Cell.DEFAULT]);
        btns_grid[x][y].setPressedIcon(icon_cell[Cell.ZERO]);
        
        // btns_grid[x][y].setMargin(new Insets(0, 0, 0, 0));
        
        // remove border
        btns_grid[x][y].setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // disable focusing
        btns_grid[x][y].setFocusable(false);
        
        // make it respond to the appropriate mouse events
        btns_grid[x][y].addMouseListener(this);
        
        // add this button to the panel
        gridPanel.add(btns_grid[x][y]);
        }// end for y
      }// end for x
      
    // change the face icon
    btn_face.setIcon(icon_face[DEFAULT]);
    
    // resize the frame
    frame.pack();
    
    // frame.repaint();
    
    /* //does not work properly just yet
     * try
     * {
     * writePreferences();
     * }
     * catch (IOException e)
     * {
     * ;
     * } */
    }// end newGame()
    
  public void recordScore(Score score) throws IOException
    {
    // use java.util.Preferences in future releases
    
    /* ****************************
     * WRITING TO FILE
     * **************************** */
    
    PrintWriter out = new PrintWriter(new FileWriter("scores.dat", true));
    
    // append the score to the file
    out.println(score.toString());
    
    // close file
    out.close();
    }// end recordScore()
    
  public void resetScores() throws IOException
    {
    // delete scores file
    (new File("scores.dat")).delete();
    
    updateScores();
    }// end resetScores()
    
  public void revealCell(int x, int y)
    {
    // IF flagged
    if (grid.getCellState(x, y) == Cell.FLAGGED)
      {
      // do not reveal
      return;
      }// end if
      
    // ELSE
    
    // IF this is the first move
    if (grid.getGameState() == Minefield.PENDING)
      {
      // start the timer
      timer.start();
      }// end if
      
    // reveal this cell
    grid.revealCell(x, y);
    
    // check for win
    checkAndDoWin();
    
    // change the cell icon
    updateGrid();
    // btns_grid[x][y].setIcon(icon_cell[grid.getCellState(x,y)]);
    // btns_grid[x][y].setPressedIcon(icon_cell[grid.getCellState(x,y)]);
    
    // IF the game state has changed to LOST
    if (grid.getGameState() == Minefield.LOST)
      {
      lose();
      }// end if
    }// end revealCell()
    
  public void revealSurroundingCells(int x, int y)
    {
    if (grid.revealSurroundingCells(x, y))
      {
      updateGrid();
      
      checkAndDoWin();
      
      // IF win
      if (grid.getGameState() == Minefield.WON)
        {
        try
          {
          win();
          }
        catch (IOException e)
          {
          ;
          }
        }
      // ELSEIF lose
      else if (grid.getGameState() == Minefield.LOST)
        {
        lose();
        }// end if
      }// end if
      
    }// end revealSurroundingCells()
    
  public void setComponentEnabled(boolean isEnabled, Component component)
    {
    component.setEnabled(isEnabled);
    
    // IF this component is a container
    if (component instanceof Container)
      {
      // DO for each component in the container
      for (Component child : ((Container) component).getComponents())
        {
        setComponentEnabled(isEnabled, child);
        }// end for child:container components
      }// end if
    }// end disableComponent()
    
  public void showAbout()
    {
    String text = "Losemine: a Windows XP Minesweeper (\"Winmine\") clone. A component of Losedoze.\n" +
      "Version " +
      VERSION +
      "\n" +
      "(C)Copyright 2012 tPenguinLTG\n";
    
    JOptionPane
      .showMessageDialog(frame, text, "About Minesweeper",
                         JOptionPane.INFORMATION_MESSAGE, icon_favicon);
    }// end showAbout()
    
  public void showScores() throws IOException
    {
    updateScores();
    dialog_scores.setVisible(true);
    }// end
    
  public void toggleFlag(int x, int y)
    {
    grid.toggleMark(x, y);
    
    if (grid.getCellState(x, y) == Cell.FLAGGED)
      {
      btns_grid[x][y].setIcon(icon_cell[Cell.FLAGGED]);
      btns_grid[x][y].setPressedIcon(icon_cell[Cell.FLAGGED]);
      }
    else if (grid.getCellState(x, y) == Cell.QUESTIONED)
      {
      btns_grid[x][y].setIcon(icon_cell[Cell.QUESTIONED]);
      btns_grid[x][y].setPressedIcon(icon_cell[Cell.DEFAULT]);
      }
    else if (grid.getCellState(x, y) == Cell.DEFAULT)
      {
      btns_grid[x][y].setIcon(icon_cell[Cell.DEFAULT]);
      btns_grid[x][y].setPressedIcon(icon_cell[Cell.DEFAULT]);
      }// end if
      
    updateFlagsLeftDisplay();
    }// end toggleFlag()
    
  public void updateFlagsLeftDisplay()
    {
    NumberFormat format = NumberFormat.getIntegerInstance();
    String displayedCount;
    
    
    // IF the flags left count is negative
    if (grid.getFlagsLeftCount() < 0)
      {
      format.setMinimumIntegerDigits(lbl_flagsLeft.length - 1);
      
      displayedCount = format.format(grid.getFlagsLeftCount());
      
      // the negative sign
      lbl_flagsLeft[0].setIcon(icon_numbers[10]);
      
      // DO for each digit
      for (int digit = 1; digit < lbl_timer.length; digit++)
        {
        // display it
        int digitDisplayed = Integer
          .parseInt(displayedCount.charAt(digit) + "");
        lbl_flagsLeft[digit].setIcon(icon_numbers[digitDisplayed]);
        }// end for digit
      }
    else
      {
      format.setMinimumIntegerDigits(lbl_flagsLeft.length);
      
      displayedCount = format.format(grid.getFlagsLeftCount());
      
      // DO for each digit
      for (int digit = 0; digit < lbl_timer.length; digit++)
        {
        // display it
        int digitDisplayed = Integer
          .parseInt(displayedCount.charAt(digit) + "");
        lbl_flagsLeft[digit].setIcon(icon_numbers[digitDisplayed]);
        }// end for digit
        
      }// end if
      
    }// end updateFlagsLeft()
    
  public void updateFrame()
    {
    updateFrame(mGameItems[COLOR].isSelected());
    }// end updateFrame()
    
  public void updateFrame(boolean useColor)
    {
    if (useColor)
      {
      // use color icons
      icon_favicon = icon_favicon_color;
      icon_cell = icon_cell_color;
      icon_face = icon_face_color;
      icon_numbers = icon_numbers_color;
      }
    else
      {
      // use B&W icons
      icon_favicon = icon_favicon_bw;
      icon_cell = icon_cell_bw;
      icon_face = icon_face_bw;
      icon_numbers = icon_numbers_bw;
      }// end if
      
    frame.setIconImage(icon_favicon.getImage());
    updateFlagsLeftDisplay();
    timer.updateTimerDisplay();
    updateGrid();
    
    // update face icon
    if (grid.getGameState() == Minefield.LOST)
      {
      btn_face.setIcon(icon_face[LOSE]);
      }
    else if (grid.getGameState() == Minefield.WON)
      {
      btn_face.setIcon(icon_face[WIN]);
      }
    else
      {
      btn_face.setIcon(icon_face[DEFAULT]);
      }
    btn_face.setPressedIcon(icon_face[PRESSED]);
    }// end updateFrame(boolean)
    
  public void updateGrid()
    {
    
    
    for (int x = 0; x < width; x++)
      {
      for (int y = 0; y < height; y++)
        {
        btns_grid[x][y].setIcon(icon_cell[grid.getCellState(x, y)]);
        
        if (grid.getCellState(x, y) == Cell.DEFAULT)
          {
          btns_grid[x][y].setPressedIcon(icon_cell[Cell.ZERO]);
          btns_grid[x][y].setDisabledIcon(icon_cell[Cell.DEFAULT]);
          }
        else
          {
          btns_grid[x][y].setPressedIcon(icon_cell[grid.getCellState(x, y)]);
          btns_grid[x][y].setDisabledIcon(icon_cell[grid.getCellState(x, y)]);
          }// end if
          
        }// end for y
      }// end for x
      
    }// end updateGrid()
    
  public void updateScores() throws IOException
    {
    highScores = getScores();
    sortedScores = sortScores(highScores);
    
    for (int difficultyLevel = 0; difficultyLevel < sortedScores.length; difficultyLevel++)
      {
      Score highScore = sortedScores[difficultyLevel][0];
      
      lbl_scores[difficultyLevel][1].setText(highScore.getScore() + " seconds");
      lbl_scores[difficultyLevel][2].setText(highScore.getName());
      }// end for
      
    }// end updateScores
    
  public void win() throws IOException
    {
    timer.stop();
    grid.setGameState(Minefield.WON);
    btn_face.setIcon(icon_face[WIN]);
    grid.flagAllMines();
    updateFlagsLeftDisplay();
    updateGrid();
    
    // check if high score
    Score score = new Score(difficulty, timer.getsecsElapsed(), "Anonymous");
    if (isHighScore(score))
      {
      // String name =
      // JOptionPane.showInputDialog(frame,"Congratulations!\nYou made a high score.\nPlease enter your name:","High Score",
      // JOptionPane.PLAIN_MESSAGE);
      String name = (String) JOptionPane
        .showInputDialog(frame,
                         "Congratulations!\nYou made a high score.\nPlease enter your name:",
                         "High Score", JOptionPane.PLAIN_MESSAGE, null, null,
                         "Anonymous");
      
      // IF the user clicked 'Cancel' or closed the dialog
      if (name == null)
        {
        return;
        }// end if
        
      score.setName(name);
      recordScore(score);
      
      // show high score table
      showScores();
      }// end if
      
    setComponentEnabled(false, gridPanel);
    }// end win()
    
  protected void setLookAndFeel()
    {
    try
      {
      // attempt to use the Windows Classic L&F
      UIManager
        .setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
      }
    catch (Exception e)
      {
      // if unsuccessful
      try
        {
        // attempt to use the Metal L&F
        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        }
      catch (Exception e1)
        {
        ;// do nothing (use default)
        }// end try..catch
      }// end try..catch
    }// end setLookAndFeel()
    
  private void getPreferences() throws IOException, FileNotFoundException
    {
    // use java.util.Preferences in future releases
    
    // default preferences
    this.difficulty = "Beginner";
    this.height = 9;
    this.width = 9;
    this.mineCount = 10;
    
    File filein = new File("losemine.ini");
    
    if (!filein.isFile())
      {
      // do nothing if file does not exist
      return;
      }// end if
      
    BufferedReader in = new BufferedReader(new FileReader(filein));
    
    while (true)
      {
      try
        {
        // will throw a NullPointerException if EOF
        String[] splitLine = in.readLine().split("=");
        
        // will throw an ArrayIndexOutOfBoundsException or
        // NumberFormatException if invalid
        if (splitLine[0].equalsIgnoreCase("height"))
          {
          this.height = Integer.parseInt(splitLine[1]);
          }
        else if (splitLine[0].equalsIgnoreCase("width"))
          {
          this.width = Integer.parseInt(splitLine[1]);
          }
        else if (splitLine[0].equalsIgnoreCase("mineCount"))
          {
          this.mineCount = Integer.parseInt(splitLine[1]);
          }
        else if (splitLine[0].equalsIgnoreCase("difficulty"))
          {
          this.difficulty = splitLine[1];
          }
        else if (splitLine[0].equalsIgnoreCase("marks"))
          {
          this.mGameItems[MARKS]
            .setSelected(Boolean.parseBoolean(splitLine[1]));
          }
        else if (splitLine[0].equalsIgnoreCase("color"))
          {
          this.mGameItems[COLOR]
            .setSelected(Boolean.parseBoolean(splitLine[1]));
          }// end if
        }
      catch (NullPointerException e)
        {
        // EOF
        break;
        }
      catch (ArrayIndexOutOfBoundsException e)
        {
        continue;
        }// end try..catch
      }// end while
      
    // close file
    in.close();
    }// end getPreferences()
    
  private LinkedList<Score> getScores() throws IOException
    {
    // use java.util.Preferences in future releases
    
    File scoreFile = new File("scores.dat");
    
    // IF this file is not a file
    if (!scoreFile.isFile())
      {
      return new LinkedList<Score>();
      }// end if
      
    /* ****************************
     * READING FROM FILE
     * **************************** */
    
    
    BufferedReader in = new BufferedReader(new FileReader(scoreFile));
    LinkedList<Score> scores = new LinkedList<Score>();
    
    while (true) // to avoid counting lines
      {
      
      try
        {
        // will throw a NullPointerException if EOF
        String[] splitLine = in.readLine().split(",");
        
        // IF the name is missing
        if (splitLine.length == 2)
          {
          // add a blank name
          splitLine = new String[] {splitLine[0], splitLine[1], " "};
          }// end if
          
        // will throw an ArrayIndexOutOfBoundsException or
        // NumberFormatException if invalid
        scores.add(new Score(splitLine[0], Integer.parseInt(splitLine[1]),
          splitLine[2]));
        }
      catch (NullPointerException e)
        {
        // EOF
        break;
        }
      catch (Exception e)
        {
        // Corrupted data
        continue;
        }// end try..catch
        
      }// end while
      
    // close file
    in.close();
    
    return scores;
    }// end getScores()
    
  private Score[][] sortScores(LinkedList<Score> unsortedScores)
                                                                throws FileNotFoundException
    {
    LinkedList<Score> sortedScoresBeginner = new LinkedList<Score>();
    LinkedList<Score> sortedScoresIntermediate = new LinkedList<Score>();
    LinkedList<Score> sortedScoresExpert = new LinkedList<Score>();
    
    // add default scores
    sortedScoresBeginner.add(new Score("Beginner", 999, "Anonymous"));
    sortedScoresIntermediate.add(new Score("Intermediate", 999, "Anonymous"));
    sortedScoresExpert.add(new Score("Expert", 999, "Anonymous"));
    
    /* ****************************
     * SORT ALGORITHM
     * **************************** */
    // sort into beginner, intermediate, and expert
    for (int scoreNumber = 0; scoreNumber < unsortedScores.size(); scoreNumber++)
      {
      
      Score score = unsortedScores.get(scoreNumber);
      
      // IF beginner
      if (score.getDifficulty().equalsIgnoreCase("Beginner"))
        {
        // add to the list of beginner scores
        sortedScoresBeginner.add(score);
        }
      
      // ELSEIF intermediate
      else if (score.getDifficulty().equalsIgnoreCase("Intermediate"))
        {
        // add to the list of intermediate scores
        sortedScoresIntermediate.add(score);
        }
      
      // ELSEIF expert
      else if (score.getDifficulty().equalsIgnoreCase("Expert"))
        {
        // add to the list of expert scores
        sortedScoresExpert.add(score);
        }// end if
        
      }// end for scoreNumber
      
      
    // DO for each difficulty level
    // beginner
    boolean isSorted = false;
    for (int i = sortedScoresBeginner.size() - 1; !isSorted && i >= 0; i--)
      {
      // assume sorted
      isSorted = true;
      
      // bubble sort
      for (int j = 0; j < i; j++)
        {
        // IF this element is greater than the next element
        if (sortedScoresBeginner.get(j).getScore() > sortedScoresBeginner
          .get(j + 1).getScore())
          {
          // swap them
          sortedScoresBeginner.add(j, sortedScoresBeginner.remove(j + 1));
          
          // signal not sorted
          isSorted = false;
          }// end if
        }// end for j
        
      // loop while not sorted
      }// end for i
      
    // Intermediate
    isSorted = false;
    for (int i = sortedScoresIntermediate.size() - 1; !isSorted && i > 0; i--)
      {
      // assume sorted
      isSorted = true;
      
      // bubble sort
      for (int j = 0; j < i; j++)
        {
        // IF this element is greater than the next element
        if (sortedScoresIntermediate.get(j).getScore() > sortedScoresIntermediate
          .get(j + 1).getScore())
          {
          // swap them
          sortedScoresIntermediate.add(j,
                                       sortedScoresIntermediate.remove(j + 1));
          
          // signal not sorted
          isSorted = false;
          }// end if
        }// end for j
        
      // loop while not sorted
      }// end for i
      
    // Expert
    isSorted = false;
    for (int i = sortedScoresExpert.size() - 1; !isSorted && i > 0; i--)
      {
      // assume sorted
      isSorted = true;
      
      // bubble sort
      for (int j = 0; j < i; j++)
        {
        // IF this element is greater than the next element
        if (sortedScoresExpert.get(j).getScore() > sortedScoresExpert
          .get(j + 1).getScore())
          {
          // swap them
          sortedScoresExpert.add(j, sortedScoresExpert.remove(j + 1));
          
          // signal not sorted
          isSorted = false;
          }// end if
        }// end for j
        
      // loop while not sorted
      }// end for i
      
      
    return new Score[][] {sortedScoresBeginner.toArray(new Score[0]),
      sortedScoresIntermediate.toArray(new Score[0]),
      sortedScoresExpert.toArray(new Score[0])};
    }// end sortScores()
    
  private void writePreferences() throws IOException
    {
    // use java.util.Preferences in future releases
    
    PrintWriter out = new PrintWriter(new FileWriter("minesweeper.ini"));
    
    out.println("height=" + height);
    out.println("width=" + width);
    out.println("mineCount=" + mineCount);
    out.println("difficulty=" + difficulty);
    out.println("marks=" + mGameItems[MARKS].isSelected());
    out.println("color=" + mGameItems[COLOR].isSelected());
    
    // close file
    out.close();
    }// end writePreferences()
  }// end Losemine
