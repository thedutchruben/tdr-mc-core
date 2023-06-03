package nl.thedutchruben.mccore.global.caching;

import nl.thedutchruben.mccore.Mccore;

import java.util.Date;

public abstract class CachingObject {

    /**
     * Returns the key/identifier of the caching object
     * 
     * @return
     */
    public abstract String getKey();

    /**
     * if true the data will also be saved on on the disk
     * 
     * @return
     */
    public abstract Boolean isPersistent();

    /**
     * Returns the date that the caching object is created
     * 
     * @return
     */
    public abstract Date getCreateDate();

    /**
     * Returns the expire date
     * 
     * @return
     */
    public abstract Date getExpireDate();

    /**
     * Returns the data that was saved
     * 
     * @return
     */
    public abstract Object getData();

    public boolean isValid() {
        if (getExpireDate() == null) {
            return true;
        }
        if (new Date().after(getExpireDate())) {
            if(isPersistent()){
                Mccore.getInstance().getCachingManager().getCachingFileSystem().removeFromFileSystem(this);
            }
            return false;
        }

        return true;
    }

    /**
     * Save the object
     */
    public void saveToDisk() {
        Mccore.getInstance().getCachingManager().getCachingFileSystem().saveToFileSystem(getKey(), this);
    }

}
