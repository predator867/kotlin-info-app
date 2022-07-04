package ar.tech.lab.status_downloader_for_whatsapp.status.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ar.tech.lab.status_downloader_for_whatsapp.status.databinding.FragmentAppBillingBinding
import com.android.billingclient.api.*

class AppBilling : Fragment() {

    private var _binding: FragmentAppBillingBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAppBillingBinding.inflate(inflater, container, false)


        // list we want tod] add for purchase
        val list = ArrayList<String>()
        list.add("android.test.purchased")

        // ini purchasesUpdatedListener
        val purchasesUpdatedListener =
            PurchasesUpdatedListener { billingResult, purchases ->
                // To be implemented in a later section.
            }

        var billingClient = BillingClient.newBuilder(requireContext())
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        binding.btnBill.setOnClickListener {

            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        // The BillingClient is ready. You can query purchases here.
                        val params = SkuDetailsParams.newBuilder()
                        params.setSkusList(list).setType(BillingClient.SkuType.INAPP)

                        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->

                            for (skuDetails in skuDetailsList!!) {
                                val flowPurchase = BillingFlowParams.newBuilder()
                                    .setSkuDetails(skuDetails)
                                    .build()

                                val responseCode =
                                    billingClient.launchBillingFlow(requireActivity(), flowPurchase)
                                        .responseCode
                            }

                        }
                    }
                }

                override fun onBillingServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                }
            })

        }



        return binding.root
    }
}