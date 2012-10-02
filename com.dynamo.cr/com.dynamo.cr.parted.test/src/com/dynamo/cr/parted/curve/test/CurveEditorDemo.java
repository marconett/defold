package com.dynamo.cr.parted.curve.test;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.DefaultOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.dynamo.cr.parted.curve.AlterSplineOperation;
import com.dynamo.cr.parted.curve.CurveEditor;
import com.dynamo.cr.parted.curve.HermiteSpline;
import com.dynamo.cr.parted.curve.ICurveEditorListener;

public class CurveEditorDemo {

    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setBackground(new Color(display, 200, 255, 255));

        final UndoContext context = new UndoContext();
        final IOperationHistory history = new DefaultOperationHistory();

        ICurveEditorListener listener = new ICurveEditorListener() {

            @Override
            public void splineChanged(String label, CurveEditor editor, HermiteSpline oldSpline,
                    HermiteSpline newSpline) {

                AlterSplineOperation operation = new AlterSplineOperation(label, editor, oldSpline, newSpline);
                operation.addContext(context);
                IStatus status = null;
                try {
                    status = history.execute(operation, null, null);
                    if (status != Status.OK_STATUS) {
                        throw new RuntimeException(status.toString());
                    }
                } catch (final ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };


        GridLayout layout = new GridLayout(1, true);
        layout.marginBottom = layout.marginTop = layout.marginLeft = layout.marginRight = 16;
        shell.setLayout(layout);
        final CurveEditor ce = new CurveEditor(shell, SWT.NONE, listener, JFaceResources.getColorRegistry());
        ce.setSpline(new HermiteSpline());
        ce.setLayoutData(new GridData(GridData.FILL_BOTH));
        ce.addFocusListener(new org.eclipse.swt.events.FocusListener() {

            @Override
            public void focusLost(org.eclipse.swt.events.FocusEvent e) {
            }

            @Override
            public void focusGained(org.eclipse.swt.events.FocusEvent e) {
                ce.redraw();
            }
        });

        ce.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {

                try {
                    if ((e.stateMask & SWT.MOD1) == SWT.MOD1) {
                        if (e.character == 'z') {
                            if ((e.stateMask & SWT.SHIFT) == SWT.SHIFT) {
                                history.redo(context,
                                        new NullProgressMonitor(), null);
                                ce.redraw();
                            } else {
                                history.undo(context,
                                        new NullProgressMonitor(), null);
                                ce.redraw();
                            }
                        }
                    }
                } catch (ExecutionException e1) {
                    e1.printStackTrace();
                }

            }
        });

        shell.pack();
        shell.setSize(640, 480);
        shell.open();

        while (!display.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();

    }

}
