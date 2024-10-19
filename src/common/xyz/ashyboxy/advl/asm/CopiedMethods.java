package xyz.ashyboxy.advl.asm;

public interface CopiedMethods {
    void copyMe();
    void copyMe2();

    interface CopiedToMethods extends CopiedMethods {
        void copied();
        void copied2();
    }
}
