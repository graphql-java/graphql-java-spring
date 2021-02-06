package testconfig.DefaultGraphQLInvocationTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.BatchLoaderWithContext;

/** A batch loader that can be declared into the DataLoader */
public class BatchLoaderImpl implements BatchLoaderWithContext<Long, String> {
	@Override
	public CompletionStage<List<String>> load(List<Long> keys, BatchLoaderEnvironment environment) {
		return CompletableFuture.supplyAsync(() -> {
			List<String> ret = new ArrayList<>(keys.size());
			for (Long key : keys) {
				ret.add(key.toString());
			}
			return ret;
		});
	}
}

