// IMusicService.aidl
package com.prsioner.androidaidldemo;

// Declare any non-default types here with import statements
import com.prsioner.androidaidldemo.Person;
interface IMusicService {
        void savePersonInfo(in Person person);
        List<Person> getAllPerson();
}
