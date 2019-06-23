package endergeticexpansion.common.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import endergeticexpansion.common.tileentities.TileEntityFrisbloomStem;
import endergeticexpansion.core.registry.EEBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext.Builder;

public class BlockFrisbloomStem extends Block {
	//The height position of the current stem; 5 layers total
	public static final IntegerProperty LAYER = IntegerProperty.create("layer", 0, 4);
	protected static final VoxelShape SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
	
	public BlockFrisbloomStem(Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState()
			.with(LAYER, 0)
		);
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder) {
		Random rand = new Random();
		ArrayList<ItemStack> dropList = new ArrayList<ItemStack>();
		if(state.get(LAYER) == 4) {
			for(int i = 0; i <= rand.nextInt(3) + 2; i++) {
				dropList.add(new ItemStack(EEBlocks.FRISBLOOM_BUD));
			}
		}
		return dropList;
	}
	
	@Override
	public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
		return SHAPE;
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(LAYER);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
		if (!worldIn.isBlockLoaded(pos)) return;
		
		if (!state.isValidPosition(worldIn, pos)) {
			worldIn.destroyBlock(pos, true);
		}
	}
	
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		BlockState ground = worldIn.getBlockState(pos.down());
		return ground.getBlock() == Blocks.END_STONE || ground.getBlock() == EEBlocks.FRISBLOOM_STEM && !worldIn.getBlockState(pos.up()).getMaterial().isLiquid();
	}
	
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 60 + worldIn.getRandom().nextInt(40));
		
		return isValidPosition(stateIn, worldIn, currentPos) ? worldIn.getBlockState(currentPos) : Blocks.AIR.getDefaultState();
	}
	
	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getPos().down();
		if(world.getBlockState(pos).getBlock() == Blocks.END_STONE) {
			return this.getDefaultState();
		} else if(world.getBlockState(pos).getBlock() == this) {
			return world.getBlockState(pos).with(LAYER, world.getBlockState(pos).get(LAYER) + 1);
		}
		return null;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityFrisbloomStem();
	}
	
	@Override
	public boolean isSolid(BlockState state) {
		return false;
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
}
