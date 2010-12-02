package matt.web;

import java.awt.Component;
//import java.awt.List;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import javax.swing.JButton;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import matt.GUI;


// Represents items in the list that can be selected
public class CheckList extends JFrame
{
    
    public wfl wf = new wfl();

    @Override
    public void setVisible(final boolean visible) {
      // make sure that frame is marked as not disposed if it is asked to be visible
      if (visible) {
          //setDisposed(false);
      }
      // let's handle visibility...
      if (!visible || !isVisible()) { // have to check this condition simply because super.setVisible(true) invokes toFront if frame was already visible
          super.setVisible(visible);
      }
      // ...and bring frame to the front.. in a strange and weird way
      if (visible) {
          int state = super.getExtendedState();
          state &= ~JFrame.ICONIFIED;
          super.setExtendedState(state);
          super.setAlwaysOnTop(true);
          super.toFront();
          super.requestFocus();
          super.setAlwaysOnTop(false);
      }
    }

    @Override
    public void toFront() {
      super.setVisible(true);
      int state = super.getExtendedState();
      state &= ~JFrame.ICONIFIED;
      super.setExtendedState(state);
      super.setAlwaysOnTop(true);
      super.toFront();
      super.requestFocus();
      super.setAlwaysOnTop(false);
    }
   protected GUI gui;

   public void setGui(GUI mattGui) {
       this.gui = mattGui;
   }

   private CheckListItem cli[];
   CheckList(String[] obs)
   {
       this.addWindowFocusListener(wf);
    // create from input
     cli = new CheckListItem[obs.length];
     for (int i = 0; i < obs.length; i++) {
         cli[i] = new CheckListItem(obs[i].toString());
     }

     JList list = new JList(cli);

      // Use a CheckListRenderer (see below)
      // to renderer list cells

      list.setCellRenderer(new CheckListRenderer());
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (UnsupportedLookAndFeelException e) {
            // handle exception
        } catch (ClassNotFoundException e) {
            // handle exception
        } catch (InstantiationException e) {
            // handle exception
        } catch (IllegalAccessException e) {
            // handle exception
        }
      // Add a mouse listener to handle changing selection
      cli[0].setSelected(true);
      list.addMouseListener(new MouseAdapter()
      {
         public void mouseClicked(MouseEvent event)
         {
            JList list = (JList) event.getSource();

            // Get index of item clicked

            int index = list.locationToIndex(event.getPoint());
            CheckListItem item = (CheckListItem)
               list.getModel().getElementAt(index);

            // Toggle selected state

            item.setSelected(! item.isSelected());
              if(item.label == null ? "All" == null : item.label.equals("All")) {
                  if(item.isSelected()) {
                     for (int i = 1; i < cli.length; i++) {
                         cli[i].setSelected(false);
                         list.repaint(list.getCellBounds(i, i));
                     }
                  }
              }
              else {
                  cli[0].setSelected(false);
                  list.repaint(list.getCellBounds(0, 0));
              }
            // Repaint cell

            list.repaint(list.getCellBounds(index, index));
         }
      });

     this.add(new JScrollPane(list));
     this.pack();


     //this.setVisible(true);
   }
 public class wfl implements WindowFocusListener {

    public void wfl() {
        
    }

    public void windowGainedFocus(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void windowLostFocus(WindowEvent e) {
        gui.setBns();
    }

 }

   public String getVals(){
       String selected = ""; // = new String[1];
       //ArrayList selected = new ArrayList();
       for (int i = 0; i < cli.length; i++) {
            if (cli[i].isSelected()) {
                selected += (i);
                selected += ",";
           }
       }
       selected = selected.substring(0, selected.length()-1);
       System.out.println(selected);
       return selected;
   }

   public String getWhat(){
       String selected = "";
       int selectedCount = 0;
       for (int i = 0; i < cli.length; i++) {
            if (cli[i].isSelected()) {
                selected = cli[i].toString();
                selectedCount++;
           }
       }
       if (cli[0].isSelected())
           return "All";
       else if(selectedCount > 1)
           return "Many";
       else if (selectedCount == 1)
           return selected;
       else
           return "None";
   }
}

class CheckListItem
{
   public String  label;
   private boolean isSelected = false;

   public CheckListItem(String label)
   {
      this.label = label;

                          try {
                        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                            if ("Nimbus".equals(info.getName())) {
                                UIManager.setLookAndFeel(info.getClassName());
                                break;
                            }
                        }
                    } catch (UnsupportedLookAndFeelException e) {
                        // handle exception
                    } catch (ClassNotFoundException e) {
                        // handle exception
                    } catch (InstantiationException e) {
                        // handle exception
                    } catch (IllegalAccessException e) {
                        // handle exception
                    }
   }

   public boolean isSelected()
   {
      return isSelected;
   }

   public void setSelected(boolean isSelected)
   {
      this.isSelected = isSelected;
   }

   public String toString()
   {
      return label;
   }
}

// Handles rendering cells in the list using a check box

class CheckListRenderer extends JCheckBox
   implements ListCellRenderer
{

   public Component getListCellRendererComponent(
         JList list, Object value, int index,
         boolean isSelected, boolean hasFocus)
   {
      setEnabled(list.isEnabled());
      setSelected(((CheckListItem)value).isSelected());
      setFont(list.getFont());
      setBackground(list.getBackground());
      setForeground(list.getForeground());
      setText(value.toString());
      return this;
   }
}