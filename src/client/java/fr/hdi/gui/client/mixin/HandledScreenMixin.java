package fr.hdi.gui.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fr.hdi.gui.client.screen.widget.UtilsWidgets;
import fr.hdi.gui.screen.utils.GuiSlot;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
    @Shadow
    protected int x;

    @Shadow
    protected int y;

    @Shadow
    protected Slot focusedSlot;

    @Inject(method = "isPointOverSlot", at = @At("HEAD"), cancellable = true)
    private void guiHelper$clipPointOverSlot(Slot slot, double pointX, double pointY, CallbackInfoReturnable<Boolean> cir) {
        if (slot instanceof GuiSlot guiSlot && !guiSlot.isInClientClip(pointX, pointY)) cir.setReturnValue(false);
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlotHighlight(Lnet/minecraft/client/gui/DrawContext;III)V"))
    private void guiHelper$clipSlotHighlight(DrawContext context, int slotX, int slotY, int z, Operation<Void> original) {
        if (!(focusedSlot instanceof GuiSlot guiSlot) || !guiSlot.hasClientClip()) {
            original.call(context, slotX, slotY, z);

            return;
        }

        UtilsWidgets.enableScissor(context, guiSlot.getClipStartX() - x, guiSlot.getClipStartY() - y, guiSlot.getClipEndX() - x, guiSlot.getClipEndY() - y);
        original.call(context, slotX, slotY, z);
        context.disableScissor();
    }
}
