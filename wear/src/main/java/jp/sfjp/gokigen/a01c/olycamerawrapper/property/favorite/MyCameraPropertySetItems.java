package jp.sfjp.gokigen.a01c.olycamerawrapper.property.favorite;

class MyCameraPropertySetItems
{
    private final String itemId;
    private final String itemName;
    private final String itemInfo;
    private final int iconResource;

    MyCameraPropertySetItems(int iconResource, String itemId, String itemName, String itemInfo)
    {
        this.iconResource = iconResource;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemInfo = itemInfo;
    }

    String getItemId()
    {
        return itemId;
    }

    String getItemName()
    {
        return itemName;
    }

    String getItemInfo()
    {
        return itemInfo;
    }

    int getIconResource()
    {
        return iconResource;
    }
}
