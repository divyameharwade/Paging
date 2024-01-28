
/*
This is an interface that is used for by all the pagereplacement algorithms
due to the fact that they own a similar structure and methods  making it simple
and easy to invoke.
 */
public interface ReplacementStrategy {
    public void referencePage(Page p);
    public Page evictPage();

    public double stats();
}
