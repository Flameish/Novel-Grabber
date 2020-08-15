package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.io.IOException;
import java.util.Objects;

// https://stackoverflow.com/questions/16586562/reordering-jlist-with-drag-and-drop @aterai
@SuppressWarnings("serial")
public class ListItemTransferHandler extends TransferHandler {
    protected final DataFlavor localObjectFlavor;
    protected int[] indices;
    protected int addIndex = -1; // Location where items were added
    protected int addCount; // Number of items added.

    public ListItemTransferHandler() {
        super();
        localObjectFlavor = new DataFlavor(Object[].class, "Array of items");
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JList<?> source = (JList<?>) c;
        c.getRootPane().getGlassPane().setVisible(true);

        indices = source.getSelectedIndices();
        Object[] transferedObjects = source.getSelectedValuesList().toArray(new Object[0]);
        // return new DataHandler(transferedObjects, localObjectFlavor.getMimeType());
        return new Transferable() {
            @Override public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[] {localObjectFlavor};
            }
            @Override public boolean isDataFlavorSupported(DataFlavor flavor) {
                return Objects.equals(localObjectFlavor, flavor);
            }
            @Override public Object getTransferData(DataFlavor flavor)
                    throws UnsupportedFlavorException, IOException {
                if (isDataFlavorSupported(flavor)) {
                    return transferedObjects;
                } else {
                    throw new UnsupportedFlavorException(flavor);
                }
            }
        };
    }

    @Override
    public boolean canImport(TransferSupport info) {
        return info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
    }

    @Override
    public int getSourceActions(JComponent c) {
        Component glassPane = c.getRootPane().getGlassPane();
        glassPane.setCursor(DragSource.DefaultMoveDrop);
        return MOVE; // COPY_OR_MOVE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean importData(TransferSupport info) {
        DropLocation tdl = info.getDropLocation();
        if (!canImport(info) || !(tdl instanceof JList.DropLocation)) {
            return false;
        }

        JList.DropLocation dl = (JList.DropLocation) tdl;
        JList target = (JList) info.getComponent();
        DefaultListModel listModel = (DefaultListModel) target.getModel();
        int max = listModel.getSize();
        int index = dl.getIndex();
        index = index < 0 ? max : index; // If it is out of range, it is appended to the end
        index = Math.min(index, max);

        addIndex = index;

        try {
            Object[] values = (Object[]) info.getTransferable().getTransferData(localObjectFlavor);
            for (int i = 0; i < values.length; i++) {
                int idx = index++;
                listModel.add(idx, values[i]);
                target.addSelectionInterval(idx, idx);
            }
            addCount = values.length;
            return true;
        } catch (UnsupportedFlavorException | IOException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        c.getRootPane().getGlassPane().setVisible(false);
        cleanup(c, action == MOVE);
    }

    private void cleanup(JComponent c, boolean remove) {
        if (remove && Objects.nonNull(indices)) {
            if (addCount > 0) {
                // https://github.com/aterai/java-swing-tips/blob/master/DragSelectDropReordering/src/java/example/MainPanel.java
                for (int i = 0; i < indices.length; i++) {
                    if (indices[i] >= addIndex) {
                        indices[i] += addCount;
                    }
                }
            }
            JList source = (JList) c;
            DefaultListModel model = (DefaultListModel) source.getModel();
            for (int i = indices.length - 1; i >= 0; i--) {
                model.remove(indices[i]);
            }
        }

        indices = null;
        addCount = 0;
        addIndex = -1;
    }
}
