/**
 * MySwing: Advanced Swing Utilites
 * Copyright (C) 2005  Santhosh Kumar T
 * <p/>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * @author Santhosh Kumar T
 */
package com.jrollor.santhosh;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import sun.org.mozilla.javascript.ast.ParenthesizedExpression;

public class JResizer extends JComponent
{
   private static final long serialVersionUID = -4786914608104463223L;

   private final ArrayList<ResizeListener> resizeListeners = new ArrayList<>();

   public JResizer(Component comp)
   {
      this(comp, new DefaultResizableBorder(6));
   }

   public JResizer(Component comp, ResizableBorder border)
   {
      setLayout(new BorderLayout());
      add(comp);
      setBorder(border);
   }

   public void addResizeListener(ResizeListener resizeListener)
   {
      resizeListeners.add(resizeListener);
   }

   public void setBorder(Border border)
   {
      removeMouseListener(resizeListener);
      removeMouseMotionListener(resizeListener);
      if (border instanceof ResizableBorder)
      {
         addMouseListener(resizeListener);
         addMouseMotionListener(resizeListener);
      }
      super.setBorder(border);
   }

   public void didResized()
   {
      if (getParent() != null)
      {
         getParent().repaint();
         invalidate();
         ((JComponent) getParent()).revalidate();
         for (int i = 0; i < resizeListeners.size(); i++)
         {
            resizeListeners.get(i).resized(getBounds());
         }
      }
   }

   public void setCheckedBounds(int x, int y, int width, int height)
   {
      Dimension parentSize = getParent().getSize();
      if (x < 0)
      {
         width += x;
         x = 0;
      }
      if (y < 0)
      {
         height += y;
         y = 0;
      }
      if (x > parentSize.getWidth())
         x = (int) parentSize.getWidth();
      if (y > parentSize.getHeight())
         y = (int) parentSize.getHeight();

      if (width < 1)
         width = 1;
      if (height < 1)
         height = 1;
      
      if((x + width) > parentSize.getWidth())
         width = (int) parentSize.getWidth() - x;
      if((y + height) > parentSize.getHeight())
         height = (int) parentSize.getHeight() - y;

      setBounds(x, y, width, height);
   }

   MouseInputListener resizeListener = new MouseInputAdapter()
   {
      public void mouseMoved(MouseEvent me)
      {
         ResizableBorder border = (ResizableBorder) getBorder();
         setCursor(Cursor.getPredefinedCursor(border.getResizeCursor(me)));
      }

      public void mouseExited(MouseEvent mouseEvent)
      {
         setCursor(Cursor.getDefaultCursor());
      }

      private int cursor;
      private Point startPos = null;

      public void mousePressed(MouseEvent me)
      {
         ResizableBorder border = (ResizableBorder) getBorder();
         cursor = border.getResizeCursor(me);
         startPos = me.getPoint();
      }

      public void mouseDragged(MouseEvent me)
      {
         if (startPos != null)
         {
            int dx = me.getX() - startPos.x;
            int dy = me.getY() - startPos.y;
            switch (cursor)
            {
            case Cursor.N_RESIZE_CURSOR:
               setCheckedBounds(getX(), getY() + dy, getWidth(), getHeight() - dy);
               didResized();
               break;
            case Cursor.S_RESIZE_CURSOR:
               setCheckedBounds(getX(), getY(), getWidth(), getHeight() + dy);
               startPos = me.getPoint();
               didResized();
               break;
            case Cursor.W_RESIZE_CURSOR:
               setCheckedBounds(getX() + dx, getY(), getWidth() - dx, getHeight());
               didResized();
               break;
            case Cursor.E_RESIZE_CURSOR:
               setCheckedBounds(getX(), getY(), getWidth() + dx, getHeight());
               startPos = me.getPoint();
               didResized();
               break;
            case Cursor.NW_RESIZE_CURSOR:
               setCheckedBounds(getX() + dx, getY() + dy, getWidth() - dx, getHeight() - dy);
               didResized();
               break;
            case Cursor.NE_RESIZE_CURSOR:
               setCheckedBounds(getX(), getY() + dy, getWidth() + dx, getHeight() - dy);
               startPos = new Point(me.getX(), startPos.y);
               didResized();
               break;
            case Cursor.SW_RESIZE_CURSOR:
               setCheckedBounds(getX() + dx, getY(), getWidth() - dx, getHeight() + dy);
               startPos = new Point(startPos.x, me.getY());
               didResized();
               break;
            case Cursor.SE_RESIZE_CURSOR:
               setCheckedBounds(getX(), getY(), getWidth() + dx, getHeight() + dy);
               startPos = me.getPoint();
               didResized();
               break;
            case Cursor.MOVE_CURSOR:
               Rectangle bounds = getBounds();
               bounds.translate(dx, dy);
               setBounds(bounds);
               didResized();
            }

            // cursor shouldn't change while dragging
            setCursor(Cursor.getPredefinedCursor(cursor));
         }
      }

      public void mouseReleased(MouseEvent mouseEvent)
      {
         startPos = null;
      }
   };
}
