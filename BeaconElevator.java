package nexus;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.event.player.PlayerToggleSneakEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.utils.Config;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import cn.nukkit.util.ArrayList;


public class BeaconElevator extends PluginBase implements Listener{
	
	 /*
	 * Config:
	 * x;y;z;world : height
	 */
	
	 List<Player> ff=new List<Player>(); //출발지점을 지정하는 플레이어를 저장
	 List<Player> sf=new List<Player>(); //도착지점을 지정하는 플레이어를 저장
	 List<Player> del=new List<Player>(); //삭제하는 플레이어를 저장
	 List<Player> players=new List<Player>(); //엘리베이터에 타는 사람을 저장
	 public Config config; 
	
	 public void onEnable(){
	 	  this.getServer().getPluginManager.registerEvents(this,this);
	 	  this.getDataFolder().mkdirs();
   	  this.config=new Config(this.getDataFolder()+"/config.yml",Config.YAML);
	 }
	 
	 public void onDisable(){
	 	 this.config.save();
	 }
	 
	 @Override
   public void onSnick(PlayerToggleSneakEvent ev){
   	  Player player=ev.getPlayer();
   	  int x=player.getFloorX();
   	  int y=player.getFloorY()-1;
   	  int z=player.getFloorZ();
   	  String folder=level.getFolderName();
   	  String location=String.valueOf(x)+String.valueOf(y)+String.valueOf(z)+world;
   	  String destin=String.valueOf(x)+String.valueOf(y-height)+String.valueOf(z)+world;
   	  int height=Integer.parseInt(this.config.get(location));
   	  if(player.getLevel().getBlockIdAt(x,y,z)==Block.BEACON&&this.config.exists(location)){
   	  	 for(int e=0;e<=height;e++){
   	  	 	  player.getLevel().setBlock(new Vector3(x,y+height-1,z),Block.get(0,0),true,false);
   	  	 	  player.setMotion(new Vector3(0.1,0.5,0.1));
   	  	 	  players.add(player);
   	  	 }
   	  	 player.teleport(new Vector3(x,y+height+0.63,z));
   	  	 player.remove(player);
   	  }
   	  if(this.config.exists(destin)){
   	  	 for(int e=0;e<=height;e++){
   	  	 	  player.getLevel().setBlock(new Vector3(x,y-1,z),Block.get(0,0),true,false);
   	  	 	  player.setMotion(new Vector3(0.1,0.5,0.1));
   	  	 	  players.add(player);
   	  	 }
   	  	 player.teleport(new Vector3(x,y+height-0.63,z));
   	  	 player.remove(player);
   	  }
   }
   
   public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args){
   	  if(cmd.getName().equals("엘베")||cmd.getName().equalsIgnoreCase("el")){
   	  	 if(!sender instanceof Player){
   	  	  	 sender.sendMessage("플레이어만 사용 가능합니다.")
   	  	  	 return true;
   	  	 }
   	  	 Player player=(Player)sender;
   	  	 if(!player.isOp()){
   	  	 	  player.sendMessage("§4이 명령어를 사용할 권한이 없습니다.");
   	  	 	  return true;
   	  	 }
   	  	 if(args.length==0){
   	  	 	  player.sendMessage("§e[엘리베이터] /엘베(EL) <생성(D)||삭제(C)>");
   	  	 	  return true;
   	  	 }
   	  	 if(args[0].equals("생성")||args[0].equalsIgnoreCase("c")){
   	  	 	  ff.add(player);
   	  	 	  sf.add(player);
   	  	 	  player.sendMessage("§6[엘레베이터] 신호기(출발지점)를 클릭하고 도착지점을 터치해주세요.");
   	  	 	  return true;
   	  	 }
   	  	 if(args[0].equals("삭제")||args[0].equalsIgnoreCase("d")){
   	  	 	  del.add(player);
   	  	 	  player.sendMessage("§6[엘레베이터] 엘레베이터를 삭제하려면 삭제할 신호기(출발지점)를 터치해주세요.");
   	  	 	  return true;
   	  	 }
   	  }
   }
   
   @Override
   public void preventFallenDamage(EntityDamageEvent ev){
   	  if(event.getCause()==EntityDamageEvent.CAUSE_FALL){
   	  	 if(!ev.getEntity() instanceof Player){
   	  	 	  return;
   	  	 }
   	  	 Player player=(Player)ev.getEntity();
   	  	 if(players.exists(player)){
   	  	 	  event.setCancelled(true);
   	  	 }
   	  }
   }
   
   @Override
   public void preventFlyKick(PlayerKickEvent ev){
   	  if(players.exists(player)){
   	  	 if(ev.getReason()=="Flying is not enabled on this server"){
   	  	 	  event.setCancelled(true);
   	  	 }
   	  }
   }
   
   @Override
   public void onTouch(PlayerInteractEvent ev){
   	  Player player=ev.getPlayer();
   	  int x=event.getBlock().getFloorX();
   	  int y=event.getBlock().getFloorY();
   	  int z=event.getBlock().getFloorZ();
   	  String folder=level.getFolderName();
   	  String location=String.valueOf(x)+String.valueOf(y)+String.valueOf(z)+world;
   	  String first;
   	  int first_y;
   	  int height=Integer.parseInt(this.config.get(location));
   	  if(ff.contains(player)){
   	  	 if(!ev.getBlock().getId()==Block.BEACON){
   	  	 	  player.sendTip("§e[엘레베이터] 신호기를 터치해주세요.");
   	  	 	  ev.setCancelled(true);
   	  	 	  return;
   	  	 }
   	  	 if(this.config.exists(location)){
   	  	 	  player.sendTip("§[엘레베이터] 이미 존재하는 엘레베이터 입니다.");
   	  	 	  ev.setCancelled(true);
   	  	 	  return;
   	  	 }
   	  	 first=location;
   	  	 first_y=y;
   	  	 player.sendTip("§6[엘레베이터] 도착지점을 터치해주세요.")
   	  	 ev.setCancelled(true);
   	  	 ff.remove(player);
   	  	 return;
   	  }
   	  if(sf.contains(player)){
   	  	 if(first_y!=y){
   	  	 	  player.sendTip("§e[엘레베이터] y좌표가 다릅니다.");
   	  	 	  ev.setCancelled(true);
   	  	 	  return;
   	  	 }
   	  	 this.config.set(first,height);
   	  	 player.sendTip("§6[엘레베이터] 엘레비에터가 생성되었습니다.")
   	  	 ev.setCancelled(true);
   	  	 sf.remove(player);
   	  	 first_y=null;
   	  	 first=null;
   	  	 return;
   	  }
   	  if(del.contains(player)){
   	  	 if(!ev.getBlock().getId()==Block.BEACON){
   	  	 	  player.sendTip("§e[엘레베이터] 신호기를 터치해주세요.");
   	  	 	  ev.setCancelled(true);
   	  	 	  return;
   	  	 }
   	  	 if(this.config.exists(location)){
   	  	 	  player.sendTip("§[엘레베이터] 엘리베이터를 삭제합니다.");
   	  	 	  this.config.remove(location);
   	  	 	  ev.setCancelled(true);
   	  	 	  return;
   	  	 }
   	  	 first=null;
   	  	 first_y=null;
   	  	 ff.remove(player);
   	  	 return;
   	  }
   }
}