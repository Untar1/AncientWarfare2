package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;
import net.shadowmage.ancientwarfare.core.interfaces.IScrollableCallback;

public class CompositeScrolled extends Composite implements IScrollableCallback
{

private Scrollbar scrollbar;
int currentTop = 0;

public CompositeScrolled(int topLeftX, int topLeftY, int width, int height)
  {
  super(topLeftX, topLeftY, width, height);
  scrollbar = new Scrollbar(width-12, 0, 12, height, this);
//  this.addGuiElement(scrollbar);  
  }

@Override
public void onScrolled(int newTop)
  {
  currentTop = newTop;
  this.updateElementPositions();
  }

@Override
protected void updateElementPositions()
  { 
  scrollbar.updateRenderPosition(renderX, renderY);
  for(GuiElement element : this.elements)
    {
    element.updateRenderPosition(renderX, renderY-currentTop);
    }
  }

public void setAreaSize(int height)
  {
  this.scrollbar.setAreaSize(height);  
  }

@Override
public void render(int mouseX, int mouseY, float partialTick)
  {
  if(!isMouseOverElement(mouseX, mouseY))
    {
    mouseX = Integer.MIN_VALUE;
    mouseY = Integer.MIN_VALUE;
    } 
  setViewport();
  Minecraft.getMinecraft().renderEngine.bindTexture(backgroundTextureLocation);
  this.renderQuarteredTexture(256, 256, 0, 0, 256, 240, renderX, renderY, width, height);
  for(GuiElement element : this.elements)
    {
    if(element.renderY > renderY + height || element.renderY + element.height < renderY)
      {
      continue;//manual frustrum culling of elements, on Y axis
      }
    if(element.renderX > renderX + width || element.renderX + element.width < renderX)
      {
      continue;//manual frustrum culling of elements, on X axis
      }
    element.render(mouseX, mouseY, partialTick);
    }   
  scrollbar.render(mouseX, mouseY, partialTick);
  resetViewport();
  }

@Override
protected void addDefaultListeners()
  {
  this.addNewListener(new Listener(Listener.ALL_EVENTS)
    {
    @Override
    public boolean onEvent(ActivationEvent evt)
      {
      if((evt.type & Listener.KEY_TYPES) != 0)
        {
        for(GuiElement element : elements)
          {
          element.handleKeyboardInput(evt);
          }
        }
      else if((evt.type & Listener.MOUSE_TYPES) != 0)
        {
        if(isMouseOverElement(evt.mx, evt.my))
          {
          scrollbar.handleMouseInput(evt);
          for(GuiElement element : elements)
            {
            element.handleMouseInput(evt);
            }
          }
        else
          {
          if(evt.type==Listener.MOUSE_UP)
            {
            for(GuiElement element : elements)
              {
              element.setSelected(false);
              }
            }
          //handle mouse leaving window, cancel scrollbar interaction
          scrollbar.dragging = false;
          scrollbar.pressed = false;
          }
        }
      return true;
      }
    });
  }

}
