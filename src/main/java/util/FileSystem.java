package util;

import java.io.*;

public class FileSystem {
    public static boolean saveTo(InputStream stream, String path){
        if (stream == null || path == null || path.isEmpty()){
            return false;
        }

        File file = new File(path);
        if(!file.exists()){
            try{
                file.createNewFile();
            } catch (IOException e){
                e.printStackTrace();
            }
        }else{
            file.delete();
        }

        try {
            FileOutputStream os = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int read = 0;
            while((read = stream.read(buffer)) != -1){
                os.write(buffer, 0, read);
            }
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static void deleteDeviceStatsFile(){
  	  File file = new File(AddressUtil.getDeviceStatsFile());
  	  if(file.exists())	{
  	      file.delete();
      }
    }
	public static void createNecessaryDirs() {
        String strline = null;
        try {
        BufferedReader br = new BufferedReader(new FileReader(AddressUtil.getDirsConfigurationPath()));
        while((strline = br.readLine()) != null){
          File file = new File(strline);
            if (!file.exists()) {
                file.mkdir();
            }
        }
        br.close();
      } catch (IOException e) {}
    }
    public static void newFile(File file){
        try{
            File f = file.getParentFile();
            if(!f.exists()){
                f.mkdirs();
            }
            if(!file.exists())	file.createNewFile();
            else{
            	file.delete();
            	file.createNewFile();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void newDir(File dir){
        try{
            if(!dir.exists()) {
                dir.mkdirs();
            }
            else{
            	//FileUtils.deleteQuietly(dir);
            	//dir.mkdirs();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static boolean exists(String path){
        File file = new File(path);
        return file.exists();
    }
    public static boolean isDirectory(String path) {
        File file = new File(path);
        return file.exists() && file.isDirectory();
    }
    public static void closeReader(Reader br){
        if(br != null){
            try{
                br.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public static void closeRandomAccessFile(RandomAccessFile f){
        if (f != null){
            try{
                f.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
