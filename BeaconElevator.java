package nexus;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.event.player.PlayerToggleSneakEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.utils.Config;
import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.LinkedHashMap;



public class BeaconElevator extends PluginBase implements Listener{
	
	 /*
	 * config:
	 * x;y;z;world : height
	 */
	
	 ArrayList<Player> ff=new ArrayList<Player>(); //출발지점을 지정하는 플레이어를 저장
	 ArrayList<Player> sf=new ArrayList<Player>(); //도착지점을 지정하는 플레이어를 저장
	 LinkedHashMap<Player,Integer> first_x=new LinkedHashMap<Player,Integer>();
	 LinkedHashMap<Player,Integer> first_y=new LinkedHashMap<Player,Integer>();
	 LinkedHashMap<Player,Integer> first_z=new LinkedHashMap<Player,Integer>();
	 ArrayList<Player> del=new ArrayList<Player>(); //삭제하는 플레이어를 저장
	 ArrayList<Player> players=new ArrayList<Player>(); //엘리베이터에 타는 사람을 저장
	 public Config config; 
	
	 @Override
	 public void onEnable(){
	 	  this.getServer().getPluginManager().registerEvents(this,this);
	 	  this.getDataFolder().mkdirs();
	 	  this.config=new Config(this.getDataFolder()+"/config.yml",Config.YAML);
	 }
	 @Override
	 public void onDisable(){
	 	 this.config.save();
	 }
	 
	 @EventHandler
	 public void onSnick(PlayerToggleSneakEvent ev){
   	  Player player=ev.getPlayer();
   	  int x=player.getFloorX();
   	  int y=player.getFloorY()-1;
   	  int z=player.getFloorZ();
   	  Level level=player.getLevel();
   	  String world=level.getFolderName();
   	  String location=String.valueOf(x)+":"+String.valueOf(y)+":"+String.valueOf(z)+":"+world;
   	  if(player.getLevel().getBlockIdAt(x,y,z)==Block.BEACON){
   		 if(this.config.exists(location)){
			int height=Integer.parseInt(this.config.get(location).toString());
			for(int e=0;e<=height+110;e++){
				player.getLevel().setBlock(new Vector3(x,y+height-1,z),Block.get(0,0),true,false);
				player.setMotion(new Vector3(0,0.01,0));
				players.add(player);
			}
			players.remove(player);
			player.teleport(new Vector3(x,y+height+1.63,z));
			player.getLevel().setBlock(new Vector3(x,y+height,z),Block.get(Block.GLASS,0),true,false);
			return;
   		 }
   		
   	  }
   }
	 
	 @Override
   public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args){
   	  if(cmd.getName().equals("엘베")||cmd.getName().equalsIgnoreCase("el")){
   	  	 if(!(sender instanceof Player)){
   	  	  	 sender.sendMessage("플레이어만 사용 가능합니다.");
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
   	  		  if(ff.contains(player)){
   	  			  player.sendMessage("§e[엘레베이터] 신호기를 터치해주세요.");
   	  		  }
   	  		  if(sf.contains(player)){
 	  			  player.sendMessage("§e[엘레베이터] 도착지점을 터치해주세요.");
 	  		  }
   	  	 	  ff.add(player);
   	  	 	  sf.add(player);
   	  	 	  player.sendMessage("§6[엘레베이터] 신호기(출발지점)를 클릭한후 도착지점을 터치해주세요.");
   	  	 	  return true;
   	  	 }
   	  	 if(args[0].equals("삭제")||args[0].equalsIgnoreCase("d")){
   	  	      if(del.contains(player)){
	  			  player.sendMessage("§e[엘레베이터] 삭제할 신호기를 터치해주세요.");
	  		  }
   	  	 	  del.add(player);
   	  	 	  player.sendMessage("§6[엘레베이터] 엘레베이터를 삭제하려면 삭제할 신호기(출발지점)를 터치해주세요.");
   	  	 	  return true;
   	  	 }
   	  }
	return true;
   }
	 @EventHandler
   public void preventFallenDamage(EntityDamageEvent event){
   	  if(event.getCause()==DamageCause.FALL){
   	  	 if(!(event.getEntity() instanceof Player)){
   	  	 	  return;
   	  	 }
   	  	 Player player=(Player)event.getEntity();
   	  	 if(players.contains(player)){
   	  	 	  event.setCancelled(true);
   	  	 	  return;
   	  	 }
   	  }
   }
	 @EventHandler
   public void preventFlyKick(PlayerKickEvent ev){
	  Player player=ev.getPlayer();
   	  if(players.contains(player)){
   	  	 if(ev.getReason()=="Flying is not enabled on this server"){
   	  	 	  ev.setCancelled(true);
   	  	 	  return;
   	  	 }
   	  }
   }
	 @EventHandler
   public void onTouch(PlayerInteractEvent event){
   	  Player player=event.getPlayer();
   	  Block block=event.getBlock();
   	  int x=block.getFloorX();
   	  int y=block.getFloorY();
   	  int z=block.getFloorZ();
   	  Level level=player.getLevel();
   	  String world=level.getFolderName();
   	  String location=String.valueOf(x)+":"+String.valueOf(y)+":"+String.valueOf(z)+":"+world;
   	  int height;
   	  if(ff.contains(player)){
   		if(!(event.getBlock().getId()==Block.BEACON)){
   			player.sendTip("§e[엘레베이터] 신호기를 터치해주세요.");
   			event.setCancelled(true);
   			return;
   		}
   		 if(this.config.exists(location)){
   			player.sendTip("§e[엘레베이터] 이미 존재하는 엘레베이터 입니다.");
   			event.setCancelled(true);
   			return;
   		}
   		first_x.put(player, x);
   		first_y.put(player, y);
   		first_z.put(player, z);
   		player.sendTip("§6[엘레베이터] 도착지점을 터치해주세요.");
   		event.setCancelled(true);
   		ff.remove(player);
   		return;
   	}
   	if(sf.contains(player)){
   		if(first_x.get(player)!=x){
   			player.sendTip("§e[엘레베이터] x좌표가 다릅니다.");
   			event.setCancelled(true);
   			return;
   		}
   		if(first_z.get(player)!=z){
   			player.sendTip("§e[엘레베이터] z좌표가 다릅니다.");
   			event.setCancelled(true);
   			return;
   		}
   		height=y-first_y.get(player);
   		if(y<=first_y.get(player)){
   			player.sendTip("§e[엘레베이터] 첫번째 지정한곳보다 높아야합니다.");
   			event.setCancelled(true);
   			return;
   		}
   		this.config.set(String.valueOf(first_x.get(player))+":"+String.valueOf(first_y.get(player))+":"+String.valueOf(first_z.get(player))+":"+world,height);
   		player.sendTip("§6[엘레베이터] 엘레베이터가 생성되었습니다.");
   		event.setCancelled(true);
   		sf.remove(player);
   		first_x.remove(player);
   		first_y.remove(player);
   		first_z.remove(player);
   		return;
   	}
   	if(del.contains(player)){
   		if(!(event.getBlock().getId()==Block.BEACON)){
   			player.sendTip("§e[엘레베이터] 신호기를 터치해주세요.");
   			event.setCancelled(true);
   			return;
   		}
   		if(this.config.exists(location)){
   			player.sendTip("§6[엘레베이터] 엘레베이터를 삭제합니다.");
   			this.config.remove(location);
   			event.setCancelled(true);
   			return;
   		}
   		first_z.remove(player);
   		ff.remove(player);
   		return;
   	  }
   }
}
