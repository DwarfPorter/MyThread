package ru.geekbrains.mythread.ui.main

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import ru.geekbrains.mythread.R
import ru.geekbrains.mythread.databinding.ThreadFragmentBinding
import java.util.*
import java.util.concurrent.TimeUnit

const val TEST_BROADCAST_INTENT_FILTER = "TEST BROADCAST INTENT FILTER"
const val THREADS_FRAGMENT_BROADCAST_EXTRA = "THREADS_FRAGMENT_EXTRA"

class ThreadFragment : Fragment() {

    companion object {
        fun newInstance() = ThreadFragment()
    }

    private var counterThread = 0;

    private var _binding: ThreadFragmentBinding? = null
    private val binding get() = _binding!!

    private val testReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.getStringExtra(THREADS_FRAGMENT_BROADCAST_EXTRA)?.let {
                binding.mainContainer.addView(AppCompatTextView(context).apply {
                    text = it
                    textSize = resources.getDimension(R.dimen.main_container_text_size)
                })
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ThreadFragmentBinding.inflate(inflater, container, false)
        context?.registerReceiver(testReceiver, IntentFilter(TEST_BROADCAST_INTENT_FILTER))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            binding.textView.text = startCalculation(binding.editText.text.toString().toInt())
            binding.mainContainer.addView(AppCompatTextView(it.context).apply {
                text = getString(R.string.in_main_thread)
                textSize = resources.getDimension(R.dimen.main_container_text_size)
            })
        }

        binding.calcThreadBtn.setOnClickListener {
            Thread {
                counterThread++
                val cntrThread = counterThread
                val calculatedResult = startCalculation(binding.editText.text.toString().toInt())
                activity?.runOnUiThread {
                    binding.textView.text = calculatedResult

                    binding.mainContainer.addView(AppCompatTextView(it.context).apply {
                        text = String.format(getString(R.string.from_thread), cntrThread)
                        textSize = resources.getDimension(R.dimen.main_container_text_size)
                    })
                }
            }.start()
        }

        val handlerThread = HandlerThread(getString(R.string.my_handler_thread))
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        binding.calcThreadHandler.setOnClickListener {
            counterThread++
            val cntrThread = counterThread
            binding.mainContainer.addView(AppCompatTextView(it.context).apply {
                text = String.format(
                    getString(R.string.calculate_in_handler_thread),
                    handlerThread.name + " " + cntrThread
                )
                textSize = resources.getDimension(R.dimen.main_container_text_size)
            })

            handler.post {
                startCalculation(binding.editText.text.toString().toInt())
                binding.mainContainer.post {
                    binding.mainContainer.addView(AppCompatTextView(it.context).apply {
                        text = String.format(
                            getString(R.string.return_from_handler_thread),
                            Thread.currentThread().name + " " + cntrThread
                        )
                        textSize = resources.getDimension(R.dimen.main_container_text_size)
                    })
                }
            }
        }
        binding.jobServiceButton.setOnClickListener {
            runService()
        }

        binding.serviceButton.setOnClickListener {
            context?.let {
                it.startService(Intent(it, MyIntentService::class.java).apply {
                    putExtra(
                        DURATION,
                        binding.editText.text.toString().toInt()
                    )
                })
            }
        }

    }

    private fun runService() {
        val bundle = PersistableBundle().apply {
            putInt(DURATION, binding.editText.text.toString().toInt())
        }

        val myJobInfo =
            JobInfo.Builder(123, ComponentName(requireContext(), MyJobService::class.java))
                .setExtras(bundle)
                .setMinimumLatency(1_000)
                .setOverrideDeadline(100_000)
                .build()

        val myJobScheduler = requireContext().getSystemService(JobScheduler::class.java)
        myJobScheduler.schedule(myJobInfo)
    }

    override fun onDestroyView() {
        _binding = null
        context?.unregisterReceiver(testReceiver)
        super.onDestroyView()
    }

    private fun startCalculation(seconds: Int): CharSequence? {
        val date = Date()
        var diffInSecs: Long
        do {
            val currentDate = Date()
            val diffInMs: Long = currentDate.time - date.time
            diffInSecs = TimeUnit.MILLISECONDS.toSeconds(diffInMs)
        } while (diffInSecs < seconds)
        return diffInSecs.toString()
    }
}