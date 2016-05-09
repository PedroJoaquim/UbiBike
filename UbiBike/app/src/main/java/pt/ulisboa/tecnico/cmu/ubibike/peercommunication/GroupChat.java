package pt.ulisboa.tecnico.cmu.ubibike.peercommunication;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupChat {

    private String mOwner;          //owner's username
    private Set<String> mMembers;   //device names

    private Chat mChat;

    public GroupChat() {
        mMembers = new HashSet<>();
        mChat = new Chat();
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public void addMember(String username){
        mMembers.add(username);
    }
    public Set<String> getMembers() {
        return mMembers;
    }

    public void setMembers(Set<String> members) {
        mMembers.addAll(members);
    }

    public boolean isEmpty(){
        return mMembers.isEmpty();
    }

    public Chat getChat(){
        return mChat;
    }

}
