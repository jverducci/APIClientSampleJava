The main() method in SampleClientDemo.java shows a very simple example of how one can integrate
with Tradeshift APIs leveraging the OAuth1 flow.  In order to run the sample demo, make sure
to replace the values of any constants in that class that have a value of "<YOUR_VALUE_HERE>".

The following constant values can be found by accessing the APIAccessToOwnAccount app in the
Tradeshift App Store:
* TOKEN
* TOKEN_SECRET
* TRADESHIFT_TENANT_ID

You will also need to provide a unique value for the "USER_AGENT" constant that will uniquely
identify you as an API user.  In addition, the demo code makes the assumption that you have
previously accepted an invoice from another company in your network.

NOTE: This is not intended to be a fully scalable out-of-the-box client solution.  This is intended to
only be sample code for integrating with Tradeshift.
