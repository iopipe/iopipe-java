package com.iopipe.elsewhere;

/**
 * A simple POJO for testing.
 *
 * @since 2018/08/20
 */
public class SimplePOJO
{
	private String favoriteanimal;
	
	private String favoritefood;
	
	private int favoritenumber;
	
	public String dayofweek;
	
	public String getFavoriteAnimal()
	{
		return this.favoriteanimal;
	}
	
	public String getFavoriteFood()
	{
		return this.favoritefood;
	}
	
	public int getFavoriteNumber()
	{
		return this.favoritenumber;
	}
	
	public void setFavoriteAnimal(String __v)
	{
		this.favoriteanimal = __v;
	}
	
	public void setFavoriteFood(String __v)
	{
		this.favoritefood = __v;
	}
	
	public void setFavoriteNumber(int __v)
	{
		this.favoritenumber = __v;
	}
}

