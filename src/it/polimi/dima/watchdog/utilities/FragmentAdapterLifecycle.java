package it.polimi.dima.watchdog.utilities;

/**
 * Tutti i fragment nel TabsAdapter devono implementare questa interfaccia!
 * @author claudio
 *
 */
public interface FragmentAdapterLifecycle {

	void onResumeFragment();

	void onPauseFragment();

}
