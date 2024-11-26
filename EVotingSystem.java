import java.io.*;
import java.util.*;

class Voter {
    private String name;
    private String id;
    private String pwd; // pwd means password

    public Voter(String name, String id, String pwd) {
        this.name = name;
        this.id = id;
        this.pwd = pwd;
    }

    public String getId() {
        return id;
    }

    public String getPwd() {
        return pwd;
    }

    public boolean checkPwd(String pwd) {
        return this.pwd.equals(pwd);
    }
}

class Candidate {
    private String name;
    private String id;
    private int votes;

    public Candidate(String name, String id) {
        this.name = name;
        this.id = id;
        this.votes = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addVote() {
        votes++;
    }

    public int getVotes() {
        return votes;
    }
}

class Election {
    private Map<String, Voter> voters = new HashMap<>();
    private Map<String, Candidate> candidates = new HashMap<>();
    private Set<String> voted = new HashSet<>();

    private static final String VOTER_FILE = "voters.txt";
    private static final String CANDIDATE_FILE = "candidates.txt";
    private static final String VOTED_FILE = "voted.txt";
    private static final String ADMIN_PWD = "admin";  // Fixed admin password

    // Load data from files
    public void loadData() {
        loadVoters();
        loadCandidates();
        loadVoted();
    }

    // Save data to files
    public void saveData() {
        saveVoters();
        saveCandidates();
        saveVoted();
    }

    private void loadVoters() {
        File file = new File(VOTER_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    voters.put(data[0], new Voter(data[1], data[0], data[2]));
                }
            }
        } catch (IOException e) {
           
        }
    }

    private void saveVoters() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(VOTER_FILE))) {
            for (Voter v : voters.values()) {
                bw.write(v.getId() + "," + v.getPwd());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving voters: " + e.getMessage());
        }
    }

    private void loadCandidates() {
        File file = new File(CANDIDATE_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    Candidate c = new Candidate(data[1], data[0]);
                    for (int i = 0; i < Integer.parseInt(data[2]); i++) {
                        c.addVote();
                    }
                    candidates.put(data[0], c);
                }
            }
        } catch (IOException | NumberFormatException e) {
            
        }
    }

    private void saveCandidates() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CANDIDATE_FILE))) {
            for (Candidate c : candidates.values()) {
                bw.write(c.getId() + "," + c.getVotes());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving candidates: " + e.getMessage());
        }
    }

    private void loadVoted() {
        File file = new File(VOTED_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                voted.add(line.trim());
            }
        } catch (IOException e) {
          
        }
    }

    private void saveVoted() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(VOTED_FILE))) {
            for (String id : voted) {
                bw.write(id);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving voted list: " + e.getMessage());
        }
    }

    public void registerVoter(String name, String id, String pwd) {
        if (!voters.containsKey(id)) {
            voters.put(id, new Voter(name, id, pwd));
        }
    }

    public void registerCandidate(String name, String id) {
        if (!candidates.containsKey(id)) {
            candidates.put(id, new Candidate(name, id));
        }
    }

    public Voter authenticateVoter(String id, String pwd) {
        Voter v = voters.get(id);
        if (v != null && v.checkPwd(pwd)) {
            return v;
        }
        return null;
    }

    public boolean castVote(Voter v, String candidateId) {
        if (voted.contains(v.getId())) return false;

        Candidate c = candidates.get(candidateId);
        if (c != null) {
            c.addVote();
            voted.add(v.getId());
            return true;
        }
        return false;
    }

    public void showResults() {
        for (Candidate c : candidates.values()) {
            System.out.println(c.getName() +"("+ c.getId()+")" + ", Votes: " + c.getVotes());
        }
    }

    public boolean adminLogin(String pwd) {
        return ADMIN_PWD.equals(pwd);
    }
}

public class EVotingSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Election election = new Election();
        election.loadData();

        while (true) {
            System.out.println("\n*** E-Voting System ***");
            System.out.println("1. Register Voter");
            System.out.println("2. Register Candidate");
            System.out.println("3. Voter Login & Vote");
            System.out.println("4. Admin Login");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();  

            switch (choice) {
                case 1:
                    System.out.print("Enter voter name: ");
                    String voterName = sc.nextLine();
                    System.out.print("Enter voter ID: ");
                    String voterId = sc.nextLine();
                    System.out.print("Enter voter password: ");
                    String voterPwd = sc.nextLine();
                    election.registerVoter(voterName, voterId, voterPwd);
                    break;

                case 2:
                    System.out.print("Enter candidate name: ");
                    String candidateName = sc.nextLine();
                    System.out.print("Enter candidate ID: ");
                    String candidateId = sc.nextLine();
                    election.registerCandidate(candidateName, candidateId);
                    break;

                case 3:
                    System.out.print("Enter voter ID: ");
                    String loginId = sc.nextLine();
                    System.out.print("Enter password: ");
                    String loginPwd = sc.nextLine();
                    Voter loggedInVoter = election.authenticateVoter(loginId, loginPwd);
                    if (loggedInVoter != null) {
                        System.out.println("Login successful!");
                        System.out.print("Enter candidate ID to vote for: ");
                        String voteId = sc.nextLine();
                        boolean voteSuccess = election.castVote(loggedInVoter, voteId);
                        if (voteSuccess) {
                            System.out.println("Vote casted successfully!");
                        } else {
                            System.out.println("Failed to cast vote.");
                        }
                    } else {
                        System.out.println("Invalid credentials.");
                    }
                    break;

                case 4:
                    System.out.print("Enter admin password: ");
                    String adminPwd = sc.nextLine();
                    if (election.adminLogin(adminPwd)) {
                        election.showResults();
                    } else {
                        System.out.println("Invalid admin password.");
                    }
                    break;

                case 5:
                    election.saveData();  
                    System.out.println("Data saved. Exiting...");
                    return;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
