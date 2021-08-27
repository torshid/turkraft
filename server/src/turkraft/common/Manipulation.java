package turkraft.common;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import turkraft.Turkraft;
import turkraft.stockers.Dinger;
import turkraft.stockers.Plot;

public class Manipulation
{
	public static String separator = "#";
	
	public static char[] chars = new char[]
	{
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
		'1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
		'!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+', '=', '/', '-', '\'', '"'
	};
	
	public static Location getLocationWorld(String text, boolean add)
	{
		if (!text.contains(separator))
		{
			return null;
		}
		
		String[] splits = text.split(separator);

		Location location = new Location(Bukkit.getWorld(splits[0]), Double.parseDouble(splits[1]), Double.parseDouble(splits[2]) + (add ? 1 : 0), Double.parseDouble(splits[3]));
		
		location.setYaw(Float.parseFloat(splits[4]));
		
		return location;
	}

	
	public static Location getLocationWorld(String text)
	{
		return getLocationWorld(text, false);
	}
	
	public static String getLocationWorld(Location location, boolean remove)
	{
		if (location == null)
		{
			return "";
		}
		
		return location.getWorld().getName() + separator + location.getX() + separator + (location.getY() + (remove ? -1 : 0)) + separator + location.getZ() + separator + location.getYaw();
	}
	
	public static String getLocationWorld(Location location)
	{
		return getLocationWorld(location, false);
	}
	
	public static Dinger getDinger(String text, boolean toTimeL)
	{
		String[] splits = text.split(separator);

		if (!toTimeL)
			return new Dinger(Integer.parseInt(splits[0]), Integer.parseInt(splits[1]));

		return new Dinger(Integer.parseInt(splits[0]) * 20, Integer.parseInt(splits[1]) * 20);
	}
	
	public static String getDinger(Dinger dinger)
	{
		return dinger.x + separator + dinger.y;
	}
	
	public static int random(int min, int max)
	{
		return (int)(Math.random() * (max - min)) + min;
	}
	
	public static boolean isTrue(String text)
	{
		return text.equalsIgnoreCase("1");
	}

	public static String getDateTime()
	{
		return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
	}

	public static String charsFromArraySeparator(String[] items, String separator)
	{
		return charsFromArraySeparator(0, items, separator);
	}

	public static String charsFromArraySeparator(int from, String[] items, String separator)
	{
		String toReturn = "";
		
		int remove = 0;
		
		for(int i = from; i < items.length; i++)
		{
			if (items[i].equalsIgnoreCase("") || items[i].equalsIgnoreCase(" "))
			{
				remove ++;
				
				continue;
			}
			
			toReturn += items[i] + separator;
		}
		
		if (items.length - remove > 0)
		{
			toReturn = toReturn.substring(0, toReturn.length() - separator.length());
		}
		
		return toReturn;
	}
	
	public static Plot plotFromString(Turkraft main, String configuration)
	{
		String[] split = configuration.split(separator);
		
		String[] point1 = split[1].split(":");

		String[] point2 = split[2].split(":");
		
		return new Plot(new Location(main.getServer().getWorld(split[0]), Integer.parseInt(point1[0]), 0, Integer.parseInt(point1[1])), 
				new Location(main.getServer().getWorld(split[0]), Integer.parseInt(point2[0]), 0, Integer.parseInt(point2[1])));
	}
	
	public static String plotLocationToString(Plot plot)
	{
		return plot.pointA.getWorld().getName() + separator + plot.pointA.getBlockX() + ":" + plot.pointA.getBlockZ() + separator + plot.pointB.getBlockX() + ":" + plot.pointB.getBlockZ();
	}

    public static boolean isAnimal(EntityType Element)
    {
    	return Element == EntityType.COW || Element == EntityType.CHICKEN
    			|| Element == EntityType.PIG || Element == EntityType.SHEEP
    			|| Element == EntityType.MUSHROOM_COW || Element == EntityType.WOLF
    			|| Element == EntityType.OCELOT || Element == EntityType.SNOWMAN
    			|| Element == EntityType.BAT || Element == EntityType.IRON_GOLEM;
    }
    
    public static ArrayList<String> stringArrayFromSplit(String text, String separator)
    {
    	return new ArrayList<String>(Arrays.asList(text.split(separator)));
    }
    
    public static String chatFormat(ChatColor color, String sayer, String content)
    {
    	return chatFormat(color, sayer, content, true);
    }
    
    public static String chatFormat(ChatColor color, String sayer, String content, boolean bracket)
    {
    	if (bracket)
    		return "<" + sayer + ChatColor.WHITE + "> " + color + content;
    	else
    		return sayer + ChatColor.WHITE + " " + color + content;
    }
    
    public static String capitalizeFirst(String text)
    {
        for (int i = 0; i < text.length(); i++)
        {
            if (i == 0)
            {
            	text = String.format("%s%s", Character.toUpperCase(text.charAt(0)), text.substring(1));
            }

            if (!Character.isLetterOrDigit(text.charAt(i)))
            {
                if (i + 1 < text.length())
                {
                	text = String.format("%s%s%s", text.subSequence(0, i+1), Character.toUpperCase(text.charAt(i + 1)), text.substring(i+2));
                }
            }
        }

        return text;
    }
    
    public static boolean isSword(Material material)
    {
    	return material == Material.WOOD_SWORD || material == Material.STONE_SWORD
    			|| material == Material.IRON_SWORD || material == Material.DIAMOND_SWORD;
    }
    
    public static boolean isHoe(Material material)
    {
    	return material == Material.WOOD_HOE || material == Material.STONE_HOE
    			|| material == Material.IRON_HOE || material == Material.DIAMOND_HOE
    			|| material == Material.GOLD_HOE;
    }
    
    public static int damageFromHoe(Material material)
    {
    	if (material == Material.WOOD_HOE)
    	{
    		return random(1, 3);
    	}
    	else if (material == Material.STONE_HOE)
    	{
    		return random(1, 7);
    	}
    	else if (material == Material.GOLD_HOE)
    	{
    		return random(1, 9);
    	}
    	else if (material == Material.IRON_HOE)
    	{
    		return random(2, 14);
    	}
    	else if (material == Material.DIAMOND_HOE)
    	{
    		return random(3, 17);
    	}
    	
    	return 0;
    }
    
    public static String generateWord(int min, int max)
    {
    	String text = "";
    	
    	int count = random(min, max);
    	
    	for (int i = 0; i < count; i++)
    	{
    		text += chars[Manipulation.random(0, chars.length - 1)];
    	}
    	
    	return text;
    }
    
    public static String generateWords(int min, int max, int min2, int max2)
    {
    	String text = "";

    	int count = random(min, max);
    	
    	for (int i = 0; i < count; i++)
    	{
    		text += generateWord(min, max) + " ";
    	}
    	
    	return text;
    }
}