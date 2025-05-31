package net.runelite.client.plugins.openrl.api.rs2.widgets;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Range;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.runelite.api.FontTypeFace;
import net.runelite.api.MenuAction;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.events.MenuAutomated;
import net.runelite.client.plugins.openrl.api.input.utils.Randomizer;

@RequiredArgsConstructor
public class RS2Widget implements Widget
{
	@NonNull
	@Getter(AccessLevel.PUBLIC)
	private final Widget widget;

	@Override
	public int getId()
	{
		return widget.getId();
	}

	@Override
	public int getType()
	{
		return widget.getType();
	}

	@Override
	public void setType(int type)
	{
		widget.setType(type);
	}

	@Override
	public int getContentType()
	{
		return widget.getContentType();
	}

	@Override
	public Widget setContentType(int contentType)
	{
		return widget.setContentType(contentType);
	}

	@Override
	public int getClickMask()
	{
		return widget.getClickMask();
	}

	@Override
	public Widget setClickMask(int mask)
	{
		return widget.setClickMask(mask);
	}

	@Override
	public Widget getParent()
	{
		return widget.getParent();
	}

	@Override
	public int getParentId()
	{
		return widget.getParentId();
	}

	@Override
	public RS2Widget[] getChildren()
	{
		final Widget[] children = widget.getChildren();
		return IntStream.range(0, children.length)
			.mapToObj(i -> new RS2Widget(children[i]))
			.toArray(RS2Widget[]::new);
	}

	@Override
	public void setChildren(Widget[] children)
	{
		widget.setChildren(children);
	}

	@Override
	public Widget[] getDynamicChildren()
	{
		return widget.getDynamicChildren();
	}

	@Override
	public Widget[] getStaticChildren()
	{
		return widget.getStaticChildren();
	}

	@Override
	public Widget[] getNestedChildren()
	{
		return widget.getNestedChildren();
	}

	@Override
	public int getRelativeX()
	{
		return widget.getRelativeX();
	}

	@Override
	public void setRelativeX(int x)
	{
		widget.setRelativeX(x);
	}

	@Override
	public int getRelativeY()
	{
		return widget.getRelativeY();
	}

	@Override
	public void setRelativeY(int y)
	{
		widget.setRelativeY(y);
	}

	@Override
	public void setForcedPosition(int x, int y)
	{
		widget.setForcedPosition(x, y);
	}

	@Override
	public String getText()
	{
		return widget.getText();
	}

	@Override
	public Widget setText(String text)
	{
		return widget.setText(text);
	}

	@Override
	public int getTextColor()
	{
		return widget.getTextColor();
	}

	@Override
	public Widget setTextColor(int textColor)
	{
		return widget.setTextColor(textColor);
	}

	@Override
	public int getOpacity()
	{
		return widget.getOpacity();
	}

	@Override
	public Widget setOpacity(int transparency)
	{
		return widget.setOpacity(transparency);
	}

	@Override
	public String getName()
	{
		return widget.getName();
	}

	@Override
	public Widget setName(String name)
	{
		return widget.setName(name);
	}

	@Override
	public int getModelId()
	{
		return widget.getModelId();
	}

	@Override
	public Widget setModelId(int id)
	{
		return widget.setModelId(id);
	}

	@Override
	public int getModelType()
	{
		return widget.getModelType();
	}

	@Override
	public Widget setModelType(int type)
	{
		return widget.setModelType(type);
	}

	@Override
	public int getAnimationId()
	{
		return widget.getAnimationId();
	}

	@Override
	public Widget setAnimationId(int animationId)
	{
		return widget.setAnimationId(animationId);
	}

	@Override
	public @Range(from = 0, to = 2047) int getRotationX()
	{
		return widget.getRotationX();
	}

	@Override
	public Widget setRotationX(@Range(from = 0, to = 2047) int modelX)
	{
		return widget.setRotationX(modelX);
	}

	@Override
	public @Range(from = 0, to = 2047) int getRotationY()
	{
		return widget.getRotationY();
	}

	@Override
	public Widget setRotationY(@Range(from = 0, to = 2047) int modelY)
	{
		return widget.setRotationY(modelY);
	}

	@Override
	public @Range(from = 0, to = 2047) int getRotationZ()
	{
		return widget.getRotationZ();
	}

	@Override
	public Widget setRotationZ(@Range(from = 0, to = 2047) int modelZ)
	{
		return widget.setRotationZ(modelZ);
	}

	@Override
	public int getModelZoom()
	{
		return widget.getModelZoom();
	}

	@Override
	public Widget setModelZoom(int modelZoom)
	{
		return widget.setModelZoom(modelZoom);
	}

	@Override
	public int getSpriteId()
	{
		return widget.getSpriteId();
	}

	@Override
	public boolean getSpriteTiling()
	{
		return widget.getSpriteTiling();
	}

	@Override
	public Widget setSpriteTiling(boolean tiling)
	{
		return widget.setSpriteTiling(tiling);
	}

	@Override
	public Widget setSpriteId(int spriteId)
	{
		return widget.setSpriteId(spriteId);
	}

	@Override
	public boolean isHidden()
	{
		return widget.isHidden();
	}

	@Override
	public boolean isSelfHidden()
	{
		return widget.isSelfHidden();
	}

	@Override
	public Widget setHidden(boolean hidden)
	{
		return widget.setHidden(hidden);
	}

	@Override
	public int getIndex()
	{
		return widget.getIndex();
	}

	@Override
	public Point getCanvasLocation()
	{
		return widget.getCanvasLocation();
	}

	@Override
	public int getWidth()
	{
		return widget.getWidth();
	}

	@Override
	public void setWidth(int width)
	{
		widget.setWidth(width);
	}

	@Override
	public int getHeight()
	{
		return widget.getHeight();
	}

	@Override
	public void setHeight(int height)
	{
		widget.setHeight(height);
	}

	@Override
	public Rectangle getBounds()
	{
		return widget.getBounds();
	}

	@Override
	public int getItemId()
	{
		return widget.getItemId();
	}

	@Override
	public Widget setItemId(int itemId)
	{
		return widget.setItemId(itemId);
	}

	@Override
	public int getItemQuantity()
	{
		return widget.getItemQuantity();
	}

	@Override
	public Widget setItemQuantity(int quantity)
	{
		return widget.setItemQuantity(quantity);
	}

	@Override
	public boolean contains(Point point)
	{
		return widget.contains(point);
	}

	@Override
	public int getScrollX()
	{
		return widget.getScrollX();
	}

	@Override
	public Widget setScrollX(int scrollX)
	{
		return widget.setScrollX(scrollX);
	}

	@Override
	public int getScrollY()
	{
		return widget.getScrollY();
	}

	@Override
	public Widget setScrollY(int scrollY)
	{
		return widget.setScrollY(scrollY);
	}

	@Override
	public int getScrollWidth()
	{
		return widget.getScrollWidth();
	}

	@Override
	public Widget setScrollWidth(int width)
	{
		return widget.setScrollWidth(width);
	}

	@Override
	public int getScrollHeight()
	{
		return widget.getScrollHeight();
	}

	@Override
	public Widget setScrollHeight(int height)
	{
		return widget.setScrollHeight(height);
	}

	@Override
	public int getOriginalX()
	{
		return widget.getOriginalX();
	}

	@Override
	public Widget setOriginalX(int originalX)
	{
		return widget.setOriginalX(originalX);
	}

	@Override
	public int getOriginalY()
	{
		return widget.getOriginalY();
	}

	@Override
	public Widget setOriginalY(int originalY)
	{
		return widget.setOriginalY(originalY);
	}

	@Override
	public Widget setPos(int x, int y)
	{
		return widget.setPos(x, y);
	}

	@Override
	public Widget setPos(int x, int y, int xMode, int yMode)
	{
		return widget.setPos(x, y, xMode, yMode);
	}

	@Override
	public int getOriginalHeight()
	{
		return widget.getOriginalHeight();
	}

	@Override
	public Widget setOriginalHeight(int originalHeight)
	{
		return widget.setOriginalHeight(originalHeight);
	}

	@Override
	public int getOriginalWidth()
	{
		return widget.getOriginalWidth();
	}

	@Override
	public Widget setOriginalWidth(int originalWidth)
	{
		return widget.setOriginalWidth(originalWidth);
	}

	@Override
	public Widget setSize(int width, int height)
	{
		return widget.setSize(width, height);
	}

	@Override
	public Widget setSize(int width, int height, int widthMode, int heightMode)
	{
		return widget.setSize(width, height, widthMode, heightMode);
	}

	@Nullable
	@Override
	public String[] getActions()
	{
		return widget.getActions();
	}

	@Override
	public Widget createChild(int index, int type)
	{
		return widget.createChild(index, type);
	}

	@Override
	public Widget createChild(int type)
	{
		return widget.createChild(type);
	}

	@Override
	public void deleteAllChildren()
	{
		widget.deleteAllChildren();
	}

	@Override
	public void setAction(int index, String action)
	{
		widget.setAction(index, action);
	}

	@Override
	public void clearActions()
	{
		widget.clearActions();
	}

	@Override
	public void setOnOpListener(Object... args)
	{
		widget.setOnOpListener(args);
	}

	@Override
	public void setOnDialogAbortListener(Object... args)
	{
		widget.setOnDialogAbortListener(args);
	}

	@Override
	public void setOnKeyListener(Object... args)
	{
		widget.setOnKeyListener(args);
	}

	@Override
	public void setOnMouseOverListener(Object... args)
	{
		widget.setOnMouseOverListener(args);
	}

	@Override
	public void setOnMouseRepeatListener(Object... args)
	{
		widget.setOnMouseRepeatListener(args);
	}

	@Override
	public void setOnMouseLeaveListener(Object... args)
	{
		widget.setOnMouseLeaveListener(args);
	}

	@Override
	public void setOnTimerListener(Object... args)
	{
		widget.setOnTimerListener(args);
	}

	@Override
	public void setOnTargetEnterListener(Object... args)
	{
		widget.setOnTargetEnterListener(args);
	}

	@Override
	public void setOnTargetLeaveListener(Object... args)
	{
		widget.setOnTargetLeaveListener(args);
	}

	@Override
	public boolean hasListener()
	{
		return widget.hasListener();
	}

	@Override
	public Widget setHasListener(boolean hasListener)
	{
		return widget.setHasListener(hasListener);
	}

	@Override
	public boolean isIf3()
	{
		return widget.isIf3();
	}

	@Override
	public void revalidate()
	{
		widget.revalidate();
	}

	@Override
	public void revalidateScroll()
	{
		widget.revalidateScroll();
	}

	@Override
	public Object[] getOnOpListener()
	{
		return widget.getOnOpListener();
	}

	@Override
	public Object[] getOnKeyListener()
	{
		return widget.getOnKeyListener();
	}

	@Override
	public Object[] getOnLoadListener()
	{
		return widget.getOnLoadListener();
	}

	@Override
	public Object[] getOnInvTransmitListener()
	{
		return widget.getOnInvTransmitListener();
	}

	@Override
	public int getFontId()
	{
		return widget.getFontId();
	}

	@Override
	public Widget setFontId(int id)
	{
		return widget.setFontId(id);
	}

	@Override
	public int getBorderType()
	{
		return widget.getBorderType();
	}

	@Override
	public void setBorderType(int thickness)
	{
		widget.setBorderType(thickness);
	}

	@Override
	public boolean isFlippedVertically()
	{
		return widget.isFlippedVertically();
	}

	@Override
	public void setFlippedVertically(boolean flip)
	{
		widget.setFlippedVertically(flip);
	}

	@Override
	public boolean isFlippedHorizontally()
	{
		return widget.isFlippedHorizontally();
	}

	@Override
	public void setFlippedHorizontally(boolean flip)
	{
		widget.setFlippedHorizontally(flip);
	}

	@Override
	public boolean getTextShadowed()
	{
		return widget.getTextShadowed();
	}

	@Override
	public Widget setTextShadowed(boolean shadowed)
	{
		return widget.setTextShadowed(shadowed);
	}

	@Override
	public int getDragDeadZone()
	{
		return widget.getDragDeadZone();
	}

	@Override
	public void setDragDeadZone(int deadZone)
	{
		widget.setDragDeadZone(deadZone);
	}

	@Override
	public int getDragDeadTime()
	{
		return widget.getDragDeadTime();
	}

	@Override
	public void setDragDeadTime(int deadTime)
	{
		widget.setDragDeadTime(deadTime);
	}

	@Override
	public int getItemQuantityMode()
	{
		return widget.getItemQuantityMode();
	}

	@Override
	public Widget setItemQuantityMode(int itemQuantityMode)
	{
		return widget.setItemQuantityMode(itemQuantityMode);
	}

	@Override
	public int getXPositionMode()
	{
		return widget.getXPositionMode();
	}

	@Override
	public Widget setXPositionMode(int xpm)
	{
		return widget.setXPositionMode(xpm);
	}

	@Override
	public int getYPositionMode()
	{
		return widget.getYPositionMode();
	}

	@Override
	public Widget setYPositionMode(int ypm)
	{
		return widget.setYPositionMode(ypm);
	}

	@Override
	public int getLineHeight()
	{
		return widget.getLineHeight();
	}

	@Override
	public Widget setLineHeight(int lineHeight)
	{
		return widget.setLineHeight(lineHeight);
	}

	@Override
	public int getXTextAlignment()
	{
		return widget.getXTextAlignment();
	}

	@Override
	public Widget setXTextAlignment(int xta)
	{
		return widget.setXTextAlignment(xta);
	}

	@Override
	public int getYTextAlignment()
	{
		return widget.getYTextAlignment();
	}

	@Override
	public Widget setYTextAlignment(int yta)
	{
		return widget.setYTextAlignment(yta);
	}

	@Override
	public int getWidthMode()
	{
		return widget.getWidthMode();
	}

	@Override
	public Widget setWidthMode(int widthMode)
	{
		return widget.setWidthMode(widthMode);
	}

	@Override
	public int getHeightMode()
	{
		return widget.getHeightMode();
	}

	@Override
	public Widget setHeightMode(int heightMode)
	{
		return widget.setHeightMode(heightMode);
	}

	@Override
	public FontTypeFace getFont()
	{
		return widget.getFont();
	}

	@Override
	public boolean isFilled()
	{
		return widget.isFilled();
	}

	@Override
	public Widget setFilled(boolean filled)
	{
		return widget.setFilled(filled);
	}

	@Override
	public String getTargetVerb()
	{
		return widget.getTargetVerb();
	}

	@Override
	public void setTargetVerb(String targetVerb)
	{
		widget.setTargetVerb(targetVerb);
	}

	@Override
	public int getTargetPriority()
	{
		return widget.getTargetPriority();
	}

	@Override
	public void setTargetPriority(int priority)
	{
		widget.setTargetPriority(priority);
	}

	@Override
	public boolean getNoClickThrough()
	{
		return widget.getNoClickThrough();
	}

	@Override
	public void setNoClickThrough(boolean noClickThrough)
	{
		widget.setNoClickThrough(noClickThrough);
	}

	@Override
	public boolean getNoScrollThrough()
	{
		return widget.getNoScrollThrough();
	}

	@Override
	public void setNoScrollThrough(boolean noScrollThrough)
	{
		widget.setNoScrollThrough(noScrollThrough);
	}

	@Override
	public int[] getVarTransmitTrigger()
	{
		return widget.getVarTransmitTrigger();
	}

	@Override
	public void setVarTransmitTrigger(int... trigger)
	{
		widget.setVarTransmitTrigger(trigger);
	}

	@Override
	public void setOnClickListener(Object... args)
	{
		widget.setOnClickListener(args);
	}

	@Override
	public void setOnHoldListener(Object... args)
	{
		widget.setOnHoldListener(args);
	}

	@Override
	public void setOnReleaseListener(Object... args)
	{
		widget.setOnReleaseListener(args);
	}

	@Override
	public void setOnDragCompleteListener(Object... args)
	{
		widget.setOnDragCompleteListener(args);
	}

	@Override
	public void setOnDragListener(Object... args)
	{
		widget.setOnDragListener(args);
	}

	@Override
	public void setOnScrollWheelListener(Object... args)
	{
		widget.setOnScrollWheelListener(args);
	}

	@Override
	public Widget getDragParent()
	{
		return widget.getDragParent();
	}

	@Override
	public Widget setDragParent(Widget dragParent)
	{
		return widget.setDragParent(dragParent);
	}

	@Override
	public Object[] getOnVarTransmitListener()
	{
		return widget.getOnVarTransmitListener();
	}

	@Override
	public void setOnVarTransmitListener(Object... args)
	{
		widget.setOnVarTransmitListener(args);
	}

	@Override
	public RS2Widget getChild(int index)
	{
		return new RS2Widget(widget.getChild(index));
	}

	public void interact(int index)
	{
		interact(getAction(index));
	}

	public void interact(String action)
	{
		final int param0 = widget.getIndex(); // -1
		final int param1 = widget.getId();
		final MenuAction menuAction = getMenuAction(action);
		final String[] actions = widget.getActions();
		final int actionIndex = Arrays.asList(actions).indexOf(action) + 1;
		final int itemId = widget.getItemId();
		final int worldViewId = -1;
		final String option = "";
		final String target = "";
		final Point clickPoint = getClickPoint();
		final int x = clickPoint.getX();
		final int y = clickPoint.getY();
		Static.getEventBus().post(new MenuAutomated(param0, param1, menuAction, actionIndex, itemId, worldViewId, option, target, x, y));
	}

	public String getAction(int index)
	{
		final String[] actions = Arrays.stream(widget.getActions())
			.filter(a -> a != null && !a.equals("null"))
			.toArray(String[]::new);
		if (index >= 0 && index < actions.length)
		{
			return actions[index];
		}
		return "null";
	}

	public boolean hasAction(String action)
	{
		final String[] actions = widget.getActions();
		return actions != null && Arrays.asList(actions).contains(action);
	}

	public MenuAction getMenuAction(String action)
	{
		final MenuAction menuAction = Static.getClient().isWidgetSelected() ? MenuAction.WIDGET_TARGET_ON_WIDGET
			: action.equalsIgnoreCase("use") ? MenuAction.WIDGET_TARGET
			: action.equalsIgnoreCase("cast") ? MenuAction.WIDGET_TARGET /*MenuAction.WIDGET_TARGET_ON_WIDGET*/
			: MenuAction.CC_OP;
		return menuAction;
	}

	public int getMenuIdentifier(int actionIndex)
	{
		switch (widget.getType())
		{
			case WidgetType.GRAPHIC:
				return widget.getTargetVerb() == null || widget.getTargetVerb().isEmpty() || Arrays.asList(widget.getActions()).contains("Cast") && !widget.getTargetVerb().startsWith("Cast") ? actionIndex + 1 : 0;
			case WidgetType.TEXT:
				return 0;
			default:
				return actionIndex + 1;
		}
	}

	public Point getClickPoint()
	{
		return Randomizer.getRandomPointIn(widget.getBounds());
	}

	public boolean isVisible()
	{
		return Static.getClientThread().runOnClientThreadOptional(() -> !isHidden()).orElse(false);
	}
}