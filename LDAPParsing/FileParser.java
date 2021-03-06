import java.io.*;
import java.util.*;

public class FileParser {
    public static void main (String[] args) throws IOException {
        JSONParser j = new JSONParser();
        CSVParser c = new CSVParser();
        ArrayList<LDAPEntry> accounts = c.makeLDAP();
        ArrayList<Member> members = j.makeMembers();
        ArrayList<Member> account = new ArrayList<>();
        String workingDir = System.getProperty("user.dir") + "/";

        ArrayList<LDAPEntry> other = new ArrayList<>();
        ArrayList<LDAPEntry> memberAcc = new ArrayList<>();
        ArrayList<LDAPEntry> incompleteAcc = new ArrayList<>();

        for(LDAPEntry l: accounts){
            if(l.mail == "" && l.employeenumber == ""){
                incompleteAcc.add(l);
            }
            else if(isMember(l,members)){
                memberAcc.add(l);
            }
            else{
                other.add(l);
            }
        }

        String otherLDIF = "version:1\n\ndn: ou=other,dc=compsoc,dc=nuigalway,dc=ie\n" +
                "objectclass: organizationalUnit\n" +
                "objectclass: top\n" +
                "ou: other\n";
        for(LDAPEntry l: other){
            l.dn = "uid="+l.uid+",ou=other,dc=compsoc,dc=nuigalway,dc=ie";
            otherLDIF += "\n" + l.makeLDIFEntry();
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(workingDir+"other.ldif"));
            os.write(otherLDIF.getBytes(), 0, otherLDIF.length());
            System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String memLDIF = "version:1\n\ndn: ou=members,dc=compsoc,dc=nuigalway,dc=ie\n" +
                "objectclass: organizationalUnit\n" +
                "objectclass: top\n" +
                "ou:members\n";
        for(LDAPEntry l: memberAcc){
            l.dn = "uid="+l.uid+",ou=members,dc=compsoc,dc=nuigalway,dc=ie";
            memLDIF += "\n" + l.makeLDIFEntry();
        }
        os = null;
        try {
            os = new FileOutputStream(new File(workingDir+"members.ldif"));
            os.write(memLDIF.getBytes(), 0, memLDIF.length());
            System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String incomLDIF = "version:1\n\ndn: ou=incompletes,dc=compsoc,dc=nuigalway,dc=ie\n" +
                "objectclass: organizationalUnit\n" +
                "objectclass: top\n" +
                "ou: incompletes\n";
        for(LDAPEntry l: incompleteAcc){
            l.dn = "uid="+l.uid+",ou=incompletes,dc=compsoc,dc=nuigalway,dc=ie";
            incomLDIF += "\n" + l.makeLDIFEntry();
        }
        os = null;
        try {
            os = new FileOutputStream(new File(workingDir+"incompletes.ldif"));
            os.write(incomLDIF.getBytes(), 0, incomLDIF.length());
            System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Members: "+memberAcc.size());
        System.out.println("Non-Members: "+other.size());
        System.out.println("Incomplete: "+incompleteAcc.size());
    }

    public static boolean isMember(LDAPEntry l, ArrayList<Member> members){
        for(Member m: members){
            if(l.mail.contains(m.email)){
                return true;
            }
            if(l.employeenumber.contains(m.id)){
                return true;
            }
        }
        return false;
    }

    public static ArrayList<String> readFile(FileReader f) throws IOException {
        ArrayList<String> arr = new ArrayList<>();
        String str = "";

        int i = 0;
        while (i != -1) {
            while (((i=f.read()) != '\n') && i != -1) {
                str += (char) i;
            }
            arr.add(str);
            str = "";
        }
        return arr;
    }
}
