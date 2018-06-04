/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package system.info.collector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarLoader;
import org.hyperic.sigar.cmd.SigarCommandBase;

/**
 *
 * @author pedrojfmorais
 */
public class NewMain extends SigarCommandBase{
    
    static String username = System.getProperty("user.name");
    static String osname = System.getProperty("os.name");
    static String osversion = System.getProperty("os.version");
    public boolean displayTimes = true;
    private static boolean isStarted = false;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        
        isStarted = true;
        
        NewMain nm = new NewMain();
        
        nm.printAll();
        //nm.CreateFile();
        
        while(isStarted){
            
            nm.Usages();
            Thread.sleep(5000);
        }
        
    }
    
    static void stop(String[] args){
        try{
            File file = new File("foo.txt");
            file.delete();
            isStarted = false;
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    private void printAll() throws Exception{
        
        System.out.print(StringCreator());
        
    }
    
    private void CreateFile() throws Exception{
        
        PrintWriter writer = new PrintWriter("log on "+new SimpleDateFormat("dd.MM.yyyy 'at' HH.mm.ss z").format(new Date())+".txt", "UTF-8");

        writer.write(StringCreator());
        
        writer.close(); 
        
    }
    
    private String StringCreator() throws UnknownHostException, IOException, InterruptedException, SigarException{
        
        String all = "";
        
        String header = "System Information on " 
                +(new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss z").format(new Date()))
                +" ------------------------------------------------------------------ \r\n";
        all += header +"\r\n";
        
        all += "System Information --------------------------------------- \r\n"+"\r\n";
        all += "Computer Name: "+InetAddress.getLocalHost().getHostName().trim()
                +" \r\nLogged User: "+username 
                +" \r\nOperative System name: " +osname 
                +" \r\nOperative System Version: " +osversion +" \r\n"+"\r\n";
        
        String uptime = "It is: " + getCurrentTime() + 
            " \r\n" + formatUptime(sigar.getUptime().getUptime());
        all += uptime +"\r\n"+"\r\n";
        
        all += "CPU Information ------------------------------------------ \r\n"+"\r\n";
        
        
        displayTimes = false;
        Vector cpuInfo = cpu();
        
        for(int a = 0; a<cpuInfo.size();a++){
            String b = String.valueOf(cpuInfo.get(a));
            all += b+"\r\n";
        }
        
        double cpuUsage = (combined(this.sigar.getCpuPerc())*1000)/10;
        all += "\r\n CPU Usage: "+ new DecimalFormat("##.##").format(cpuUsage) +"%\r\n"+"\r\n";
        
        all += "GPU Information ------------------------------------------ \r\n"+"\r\n";
        
        all += gpu()+"\r\n";
        
        all += "RAM Information ------------------------------------------ \r\n"+"\r\n";
        
        all += ram()+"\r\n";
        
        all += "Disk Information ----------------------------------------- \r\n"+"\r\n";
        
        all += disk()+"\r\n";
        
        all += "Internet Connection -------------------------------------- \r\n"+"\r\n";
        
        // Returns the instance of InetAddress containing
        // local host name and address
        InetAddress localhost = InetAddress.getLocalHost();
        all += "System IP Address : " +
                      (localhost.getHostAddress()).trim()+"\r\n";
 
        // Find public IP address
        String systemipaddress = "";
        try{
            URL url_name = new URL("http://bot.whatismyipaddress.com");
 
            BufferedReader sc = new BufferedReader(new InputStreamReader(url_name.openStream()));
 
            // reads system IPAddress
            systemipaddress = sc.readLine().trim();
        }catch (Exception e){
            systemipaddress = "Cannot Execute Properly";
        }
        all += "Public IP Address: " + systemipaddress +"\r\n"+"\r\n";
        
        all += "--------------------------------------------------"
                + "----------------------------------------------------------- \r\n"+"\r\n";
        
        
        return all;
        
    }
    
    public void Usages() throws SigarException{
        
        String toReturn = "";
        
        //CPU
        double cpuUsage = (combined(this.sigar.getCpuPerc())*1000)/10;
        toReturn += "CPU Usage: "+ new DecimalFormat("##.##").format(cpuUsage) +"%\r\n";
        
        //RAM
        Mem mem = this.sigar.getMem();
        double memUsed = mem.getActualUsed();
        memUsed = memUsed/1000000000;
        
        double memTotal = mem.getTotal();
        memTotal = memTotal/1000000000;
        
        double RAMUsage = memUsed*100;
        RAMUsage = RAMUsage/memTotal;
        String RAMPerc = new DecimalFormat("#####.##").format(RAMUsage);
        
        toReturn += "RAM Usage: "+RAMPerc+"% \r\n";
        
        //Disk
        File f = new File("C:\\");
        
        double free = f.getFreeSpace();
        free = Double.valueOf(new DecimalFormat("#####.##").format(free));
        free = free/1000000000;
        String freeDisk = new DecimalFormat("#####.##").format(free);
        
        double total = f.getTotalSpace();
        total = Double.valueOf(new DecimalFormat("#####.##").format(total));
        total = total/1000000000;
        
        double used = f.getTotalSpace() - f.getFreeSpace();
        used = Double.valueOf(new DecimalFormat("#####.##").format(used));
        used = used/1000000000;
        
        double PercUsage = used*100;
        PercUsage = PercUsage/total;
        String usedSpace = new DecimalFormat("#####.##").format(PercUsage);
        
        toReturn += "Used Disk Space: "+usedSpace+"% \r\n";
        toReturn += "Free Disk Space: "+freeDisk+" GB";
        System.out.println(toReturn+"\r\n");
        
    }
    
    public static String gpu() throws IOException, InterruptedException{
        
        String info = "";
        
        try{
            String filePath = "foo.txt";
            // Use "dxdiag /t" variant to redirect output to a given file
            ProcessBuilder pb = new ProcessBuilder("cmd.exe","/c","dxdiag","/t",filePath);
            Process p = pb.start();
            p.waitFor();

            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            
            while((line = br.readLine()) != null){
                if(line.trim().startsWith("Card name:") || line.trim().startsWith("Current Mode:")){
                    info += line.trim()+"\r\n";
                }
            }
            p.destroy();
            
            File f = new File("foo.txt");
            f.delete();
            
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
        return info;
    }
    
    public static String disk(){
        
        File f = new File("C:\\");
        double free = f.getFreeSpace();
        free = Double.valueOf(new DecimalFormat("#####.##").format(free));
        free = free/1000000000;
        String freeDisk = new DecimalFormat("#####.##").format(free);
        
        double total = f.getTotalSpace();
        total = Double.valueOf(new DecimalFormat("#####.##").format(total));
        total = total/1000000000;
        String totalDisk = new DecimalFormat("#####.##").format(total);
        
        double used = f.getTotalSpace() - f.getFreeSpace();
        used = Double.valueOf(new DecimalFormat("#####.##").format(used));
        used = used/1000000000;
        String usedSpace = new DecimalFormat("#####.##").format(used);
                
        String returnValue = "Disk Free Space: " +freeDisk +" GB / "+totalDisk +" GB\r\n"
                +"Disk Used Space: " +usedSpace +" GB / "+totalDisk +" GB\r\n"
                +"Total Disk Space: " +totalDisk +" GB\r\n";
        
        return returnValue;
    }
    
    public String ram() throws SigarException{
       
        Mem mem = this.sigar.getMem();
        double memUsed = mem.getActualUsed();
        memUsed = memUsed/1000000000;
        String ramUsed = new DecimalFormat("#####.##").format(memUsed);
        
        double memFree = mem.getActualFree();
        memFree = memFree/1000000000;
        String ramFree = new DecimalFormat("#####.##").format(memFree);
        
        double memTotal = mem.getTotal();
        memTotal = memTotal/1000000000;
        String ramTotal = new DecimalFormat("#####.##").format(memTotal);
        
        String used = ramUsed +" GB / " + ramTotal +" GB";
        String free = ramFree +" GB / " + ramTotal +" GB";
        String total = ramTotal +" GB";
        
        return "Memory Used: "+used +"\r\nMemory Free: "+free+"\r\nTotal Memory: "+total+"\r\n";
    }

    @Override
    public void output(String[] strings) throws SigarException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    private static String formatUptime(double uptime) {
        String retval = "Computer on for: ";

        int days = (int)uptime / (60*60*24);
        int minutes, hours;

        if (days != 0) {
            retval += days-1 + " " + ((days > 1) ? "days" : "day") + " and ";
        }

        minutes = (int)uptime / 60;
        hours = minutes / 60;
        hours %= 24;
        minutes %= 60;

        if (hours != 0) {
            retval += hours + ":" + minutes +" hours.";
        }
        else {
            retval += minutes + " min";
        }

        return retval;
    }

    public Vector cpu() throws SigarException{
        Vector<String> data = new Vector<String>();
        
        org.hyperic.sigar.CpuInfo[] infos =
            this.sigar.getCpuInfoList();

        CpuPerc[] cpus =
            this.sigar.getCpuPercList();

        org.hyperic.sigar.CpuInfo info = infos[0];
        long cacheSize = info.getCacheSize();
        data.add("Vendor........." + info.getVendor());
        data.add("Model.........." + info.getModel());
        data.add("Mhz............" + info.getMhz());
        data.add("Total CPUs....." + info.getTotalCores());
        
        if (!this.displayTimes) {
            return data;
        }

        for (int i=0; i<cpus.length; i++) {
            println("CPU " + i + ".........");
            output(cpus[i]);
        }

        data.add("Totals........");
        
        return data;
    }
    
    protected void output(CpuPerc cpu) {
        println("User Time....." + CpuPerc.format(cpu.getUser()));
        println("Sys Time......" + CpuPerc.format(cpu.getSys()));
        println("Idle Time....." + CpuPerc.format(cpu.getIdle()));
        println("Wait Time....." + CpuPerc.format(cpu.getWait()));
        println("Nice Time....." + CpuPerc.format(cpu.getNice()));
        println("Combined......" + CpuPerc.format(cpu.getCombined()));
        println("Irq Time......" + CpuPerc.format(cpu.getIrq()));
        if (SigarLoader.IS_LINUX) {
            println("SoftIrq Time.." + CpuPerc.format(cpu.getSoftIrq()));
            println("Stolen Time...." + CpuPerc.format(cpu.getStolen()));
        }
        println("");
    }
    
    public double combined(CpuPerc cpu){
        
        double perc = cpu.getCombined();
        
        return perc;
    }
    
    private static String getCurrentTime() {
        return new SimpleDateFormat("h:mm a").format(new Date());
    }
    
}
