package fr.hdi.gui.gui.objects;

import fr.hdi.gui.gui.utils.ObjectRegisterType;
import fr.hdi.gui.gui.utils.ObjectType;
import fr.hdi.gui.gui.utils.Texture;
import net.minecraft.util.Identifier;
import org.w3c.dom.Text;

public abstract class GuiObject {
    private ObjectType objectType;
    private ObjectRegisterType objectRegisterType;
    private Texture texture;
    private int objectStartX;
    private int objectStartY;
    private int objectSizeX;
    private int objectSizeY;
    private Runnable onPress;
}
