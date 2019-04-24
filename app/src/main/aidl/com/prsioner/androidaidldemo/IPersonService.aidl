// IPersonService.aidl
 package com.prsioner.androidaidldemo;

 // Declare any non-default types here with import statements
 import com.prsioner.androidaidldemo.Person;
 interface IPersonService {
         void savePersonInfo(in Person person);
         List<Person> getAllPerson();
 }
