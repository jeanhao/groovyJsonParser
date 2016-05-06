package entity;


public class Person {
	def name;
	def age;
	def boolean isStudent;
	def ArrayList numbers ;
	
	def Person() {
		super();
	}
	
	
	def Person( name,  age,  isStudent,  numbers) {
		this.name = name;
		this.age = age;
		this.isStudent = isStudent;
		this.numbers = numbers;
	}


	@Override
	public String toString() {
		return "Person [name=" + name + ", age=" + age + ", isStudent="+ isStudent + ", numbers=" + numbers+ "]"
	}
}
