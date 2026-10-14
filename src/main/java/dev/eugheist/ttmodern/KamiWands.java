package dev.eugheist.ttmodern;
final class KamiWands {
 static void register(){
  var cap=new dev.eugheist.tc4modern.WandParts.Cap("ICHOR",.7f,java.util.List.of(),1f,()->KamiResources.item(KamiResources.Kind.CAP),10);
  var rod=new dev.eugheist.tc4modern.WandParts.Rod("ICHORCLOTH",1000,()->KamiResources.item(KamiResources.Kind.ROD),10,null,true);
  if(dev.eugheist.tc4modern.WandParts.CAPS.putIfAbsent(cap.tag(),cap)!=null||dev.eugheist.tc4modern.WandParts.RODDEFS.putIfAbsent(rod.tag(),rod)!=null)throw new IllegalStateException("Duplicate KAMI wand parts");
 }
}
