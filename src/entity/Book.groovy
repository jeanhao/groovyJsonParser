package entity;

import java.util.Date;

class Book {
	def name;
	def Double prize;
	def Date releaseDate;
	def Person writer;
	def ArrayList buyers ;
	
	public Book( name,  prize,  releaseDate,  writer,  buyers) {
		this.name = name;
		this.prize = prize;
		this.releaseDate = releaseDate;
		this.writer = writer;
		this.buyers = buyers;
	}
			
	public Book() {
	}

	@Override
	public String toString() {
		return "Book [name=" + name + ", prize=" + prize + ", releaseDate= "+releaseDate+ ", writer=" + writer + ", buyers=" + buyers + "]"
	}

	
}
