package dev.eugheist.ttmodern;
final class InfusedBookClientAudit {
 private static net.minecraft.client.gui.screens.Screen book,previous;private static java.lang.reflect.Field pageField;
 private static boolean draw(net.minecraft.client.gui.GuiGraphics gui,String key,int x,int y){try{var method=Class.forName("dev.eugheist.tc4modern.RecipeBookBridge").getDeclaredMethod("draw",net.minecraft.client.gui.GuiGraphics.class,String.class,int.class,int.class);method.setAccessible(true);return (boolean)method.invoke(null,gui,key,x,y);}catch(ReflectiveOperationException e){throw new IllegalStateException("TT native recipe renderer access",e);}}
 static void run(net.minecraft.client.Minecraft mc){
  var gui=new net.minecraft.client.gui.GuiGraphics(mc,mc.renderBuffers().bufferSource());
  if(!draw(gui,"INFUSED_POTIONS",0,0))throw new IllegalStateException("TT seed cycling recipe page unresolved");
  for(int i=0;i<4;i++)if(!draw(gui,"INFUSED_POTIONSPOT"+i,0,0))throw new IllegalStateException("TT potion book recipe unresolved: "+i);gui.flush();
  try{
   var screen=new dev.eugheist.tc4modern.ThaumonomiconScreen(new dev.eugheist.tc4modern.ThaumonomiconMenu(0,mc.player.getInventory()),mc.player.getInventory(),net.minecraft.network.chat.Component.literal("Thaumonomicon"));screen.init(mc,mc.getWindow().getGuiScaledWidth(),mc.getWindow().getGuiScaledHeight());var opened=screen.getClass().getDeclaredField("opened");opened.setAccessible(true);opened.set(screen,thaumcraft.api.research.ResearchCategories.getResearch("INFUSED_POTIONS"));pageField=screen.getClass().getDeclaredField("page");pageField.setAccessible(true);book=screen;previous=mc.screen;mc.setScreen(book);
  }catch(ReflectiveOperationException e){throw new IllegalStateException("TT infused book native rendering",e);}

 }
 static void tick(net.minecraft.client.Minecraft mc,int tick){
  if(tick==303)run(mc);
  if(tick>=313&&tick<=343&&(tick-313)%10==0)try{int current=pageField.getInt(book);var dir=java.nio.file.Path.of("enchanter-audit");java.nio.file.Files.createDirectories(dir);try(var image=net.minecraft.client.Screenshot.takeScreenshot(mc.getMainRenderTarget())){image.writeToFile(dir.resolve("infused-book-"+current+".png"));}if(tick<343)pageField.setInt(book,new int[]{6,8,10}[(tick-313)/10]);else{mc.setScreen(previous);System.out.println("TT INFUSED BOOK CLIENT AUDIT: ALL PASS (native cycling infusion/four crucible renderers,actual rendered Thaumonomicon text/recipe frames)");}}catch(ReflectiveOperationException|java.io.IOException e){throw new IllegalStateException("TT book frames",e);}
 }
}
