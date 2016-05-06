package entity;

import java.util.Arrays;

public class Person1 {
	private String name;
	private Integer age;
	private Boolean isStudent;
	private String[] numbers;
	
	public String[] getNumbers() {
		return numbers;
	}
	public void setNumbers(String[] numbers) {
		this.numbers = numbers;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public Boolean getIsStudent() {
		return isStudent;
	}
	public void setIsStudent(Boolean isStudent) {
		this.isStudent = isStudent;
	}
	public Person1() {
		super();
	}
	@Override
	public String toString() {
		return "Person [name=" + name + ", age=" + age + ", isStudent="
				+ isStudent + ", numbers=" + Arrays.toString(numbers) + "]";
	}
	public Person1(String name, Integer age, Boolean isStudent, String[] numbers) {
		super();
		this.name = name;
		this.age = age;
		this.isStudent = isStudent;
		this.numbers = numbers;
	}
}
