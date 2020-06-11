package org.phomellolitepos;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityManagerUtil {
    private static List<Activity> activityList = new ArrayList<Activity>();

    public static void addActivity(Activity activity)
    {
        if(activityList==null)
        {
            activityList = new ArrayList<Activity>();
        }

        if(activity!=null) {
            activityList.add(activity);
        }
    }

    // Finish and remove an activity from activityList.
    public static void finishActivity(Activity activity)
    {
        if(activityList!=null)
        {
            if(activity!=null && activityList.contains(activity)) {
                activity.finish();
                activityList.remove(activity);
            }
        }
    }

    // Finish and remove all activity in activityList.
    public static void finishAllActivity()
    {
        if(activityList!=null)
        {
            // First finish all the activity in the list.
            int size = activityList.size();
            for(int i=0;i<size;i++)
            {
                Activity activity = activityList.get(i);
                activity.finish();
            }

            // Then remove all the activity in the list.
            for(int i=0;i<size;i++)
            {
                activityList.remove(i);
            }
        }
    }
}
