package entity;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Book1 {
	private String name;
	private Double prize;
	private Date releaseDate;
	private Person1 writer;
	public Person1 getWriter() {
		return writer;
	}
	public void setWriter(Person1 writer) {
		this.writer = writer;
	}
	private Person1[] buyers;
	
	public Person1[] getBuyers() {
		return buyers;
	}
	public void setBuyers(Person1[] buyers) {
		this.buyers = buyers;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getPrize() {
		return prize;
	}
	public void setPrize(Double prize) {
		this.prize = prize;
	}
	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	public Book1(String name, Double prize, Date releaseDate, Person1 writer,
			Person1[] buyers) {
		super();
		this.name = name;
		this.prize = prize;
		this.releaseDate = releaseDate;
		this.writer = writer;
		this.buyers = buyers;
	}
	@Override
	public String toString() {
		return "Book [name=" + name + ", prize=" + prize + ", releaseDate="
				+ releaseDate + ", writer=" + writer + ", buyers="
				+ Arrays.toString(buyers) + "]";
	}
	public Book1() {
		super();
	}
}
